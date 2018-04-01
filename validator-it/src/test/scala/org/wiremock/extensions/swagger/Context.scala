package org.wiremock.extensions.swagger

import org.wiremock.extensions.swagger.components.MockComponent

trait Context {

  val mockComponent:MockComponent = MockComponent()
}
