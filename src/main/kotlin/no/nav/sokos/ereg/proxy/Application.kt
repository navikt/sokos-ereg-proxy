package no.nav.sokos.ereg.proxy

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

import no.nav.sokos.ereg.proxy.config.ApplicationState
import no.nav.sokos.ereg.proxy.config.applicationLifecycleConfig
import no.nav.sokos.ereg.proxy.config.commonConfig
import no.nav.sokos.ereg.proxy.config.routingConfig

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(true)
}

fun Application.module() {
    val applicationState = ApplicationState()

    applicationLifecycleConfig(applicationState)
    commonConfig()
    routingConfig(applicationState)
}
