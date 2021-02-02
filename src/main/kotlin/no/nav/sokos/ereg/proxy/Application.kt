package no.nav.sokos.ereg.proxy

import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.stop
import io.ktor.server.netty.Netty
import io.prometheus.client.hotspot.DefaultExports
import no.nav.sokos.ereg.proxy.api.eregProxyApi
import no.nav.sokos.ereg.proxy.api.commonFeatures
import no.nav.sokos.ereg.proxy.api.naisApi
import no.nav.sokos.ereg.proxy.api.swaggerApi
import no.nav.sokos.ereg.proxy.metrics.Metrics
import no.nav.sokos.ereg.proxy.metrics.metrics
import no.nav.sokos.ereg.proxy.ereg.EregService
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

fun main() {
    val appState = ApplicationState()
    val appConfig = Configuration()

    val httpServer = HttpServer(appState, EregService(appConfig.eregHost))

    httpServer.start()

    appState.running = true

    Runtime.getRuntime().addShutdownHook(Thread {
        appState.running = false
        httpServer.stop()
    })
}

class HttpServer(
    appState: ApplicationState,
    eregService: EregService,
    port: Int = 8080,
) {
    private val embeddedServer = embeddedServer(Netty, port) {
        commonFeatures()
        naisApi({ appState.initialized }, { appState.running })
        metrics()
        swaggerApi()
        eregProxyApi(eregService)
    }

    fun start() = embeddedServer.start()
    fun stop() = embeddedServer.stop(5, 5, TimeUnit.SECONDS)
}

class ApplicationState(
    defaultInitialized: Boolean = true,
    defaultRunning: Boolean = false
) {
    var initialized: Boolean by Delegates.observable(defaultInitialized) { _, _, newValue ->
        if (!newValue) Metrics.appStateReadyFalse.inc()
    }
    var running: Boolean by Delegates.observable(defaultRunning) { _, _, newValue ->
        if (!newValue) Metrics.appStateRunningFalse.inc()
    }
}
