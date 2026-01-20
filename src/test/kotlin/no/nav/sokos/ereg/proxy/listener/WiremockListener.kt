package no.nav.sokos.ereg.proxy.listener

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.configureFor
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec

private const val WIREMOCK_SERVER_PORT = 9001

object WiremockListener : TestListener {
    val wiremock = WireMockServer(WIREMOCK_SERVER_PORT)

    override suspend fun beforeSpec(spec: Spec) {
        configureFor(WIREMOCK_SERVER_PORT)
        wiremock.start()
    }

    override suspend fun afterSpec(spec: Spec) {
        wiremock.stop()
    }
}
