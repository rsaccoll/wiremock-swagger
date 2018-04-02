
ansiColor('xterm') {
    lock("${env.PROJECT_NAME}"){
        node ("master"){

            def sbtFolder          = "${tool name: 'sbt-0.13.13', type: 'org.jvnet.hudson.plugins.SbtPluginBuilder$SbtInstallation'}/bin"
            def projectName        = "${env.PROJECT_NAME}"
            def github_token       = "${env.GITHUB_TOKEN}"
            def jenkins_github_id  = "${env.JENKINS_GITHUB_CREDENTIALS_ID}"
            def docker_account     = "${env.AWS_ECR_DOCKER_ACCOUNT}"
            def docker_registry    = "${env.AWS_ECR_DOCKER_REGISTRY}"
            def pipeline_version   = "1.0.0-b${env.BUILD_NUMBER}"
            def github_commit      = ""

            stage("Checkout"){
                echo "git checkout"
                checkout changelog: false, poll: false, scm: [
                    $class: 'GitSCM',
                    branches: [[
                        name: 'master'
                    ]],
                    doGenerateSubmoduleConfigurations: false,
                    extensions: [[
                        $class: 'WipeWorkspace'
                    ], [
                        $class: 'CleanBeforeCheckout'
                    ]],
                    submoduleCfg: [],
                    userRemoteConfigs: [[
                        credentialsId: "${jenkins_github_id}",
                        url: "git@github.com:telegraph/${projectName}.git"
                    ]]
                ]
            }

            stage("Build & Unit Tests"){
                """
                    ${sbtFolder}/sbt clean coverage test coverageReport coverageAggregate
                target/scala-2.12/scoverage-report/index.html
                """
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/scala-2.12/scoverage-report',
                    reportFiles: 'index.html',
                    reportName: 'Coverage Report',
                    reportTitles: ''
                ])
            }

            stage("Assembly"){
                sh """
                    ${sbtFolder}/sbt "; project validator ; clean ; assembly "
                """
                dir('validator') {
                    docker.build("wiremock-swagger-validator", ".")
                }
            }

            stage("Functional Tests"){
                sh """
                    echo "Running Component Tests"
                    docker-compose -f validator-it/docker-compose.yml up
                """
                try {
                    sh """
                        ${sbtFolder}/sbt "validator-it/test"
                    """
                    junit "validator-it/target/test-reports/**/*.xml"
                }finally {
                    sh """
                        docker-compose -f validator-it/docker-compose.yml down
                    """
                }
            }

            stage("Publish"){
                docker.withRegistry("${docker_account}", "${docker_registry}") {
                    docker.image("wiremock-swagger-validator").push("wiremock-swagger/validator:latest")
                    docker.image("wiremock-swagger-validator").push("wiremock-swagger/validator:${pipeline_version}")
                }
            }

            stage("Release Notes"){
                // Possible error if there is a commit different from the trigger commit
                github_commit = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()

                //Realease on Git
                println("\n[TRACE] **** Releasing to github ${github_token}, ${pipeline_version}, ${github_commit} ****")
                sh """#!/bin/bash
                    GITHUB_COMMIT_MSG=\$(curl -H "Content-Type: application/json" -H "Authorization: token ${github_token}" https://api.github.com/repos/telegraph/${projectName}/commits/\"${github_commit}\" | /usr/local/bin/jq \'.commit.message\')
                    echo "GITHUB_COMMIT_MSG: \${GITHUB_COMMIT_MSG}"
                    echo "GITHUB_COMMIT_DONE: DONE"
                    C_DATA="{\\\"tag_name\\\": \\\"${pipeline_version}\\\",\\\"target_commitish\\\": \\\"master\\\",\\\"name\\\": \\\"${pipeline_version}\\\",\\\"body\\\": \${GITHUB_COMMIT_MSG},\\\"draft\\\": false,\\\"prerelease\\\": false}"
                    echo "C_DATA: \${C_DATA}"
                    curl -H "Content-Type: application/json" -H "Authorization: token ${github_token}" -X POST -d "\${C_DATA}" https://api.github.com/repos/telegraph/${projectName}/releases
                """
            }
        }
    }
}
