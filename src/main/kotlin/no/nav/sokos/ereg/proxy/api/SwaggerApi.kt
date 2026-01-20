package no.nav.sokos.ereg.proxy.api

import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.Route

fun Route.swaggerApi() {
    swaggerUI(
        "/organisasjon-proxy/api/v1/docs",
        "openapi/ereg-proxy_v1_002.json",
    )
}
