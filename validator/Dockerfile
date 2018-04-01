FROM openjdk:8-jre-alpine

MAINTAINER Telegraph Platforms <platforms@telegraph.co.uk>

RUN apk add --update openssl

# grab su-exec for easy step-down from root
# and bash for "bin/elasticsearch" among others
RUN apk add --no-cache 'su-exec>=0.2' bash

# grab wiremock standalone jar
RUN mkdir -p /var/wiremock/lib/ \
    mkdir -p /var/wiremock/extensions/

WORKDIR /home/wiremock

COPY scripts/docker-entrypoint.sh /
COPY target/wiremock-swagger-validator.jar /var/wiremock/lib/wiremock-standalone.jar

VOLUME /home/wiremock
EXPOSE 8080 8443

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["java", "-cp", "/var/wiremock/lib/*:/var/wiremock/extensions/*", "org.wiremock.extensions.swagger.WireMockServerRunner"]
