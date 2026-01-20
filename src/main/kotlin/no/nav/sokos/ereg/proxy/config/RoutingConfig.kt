package no.nav.sokos.ereg.proxy.config

import io.ktor.server.application.Application
import io.ktor.server.routing.routing

import no.nav.sokos.ereg.proxy.api.eregProxyApi
import no.nav.sokos.ereg.proxy.api.swaggerApi

fun Application.routingConfig(applicationState: ApplicationState) {
    routing {
        internalNaisRoutes(applicationState)
        eregProxyApi()
        swaggerApi()
    }
}
