package no.nav.sokos.ereg.proxy.ereg

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.features.json.JsonFeature
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import no.nav.sokos.ereg.proxy.asResource
import no.nav.sokos.ereg.proxy.jsonClientConfiguration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EregServiceTest {

    private val eregEndpoint = "0.0.0.0"

    @Test
    fun `Skal mappe organisasjon fra Ereg`() {
        val eregService = mockEregService("ereg_response1.json".asResource().readText())
        val organisasjon = runBlocking {
            eregService.organisasjon(
                navCallId = "TEST",
                navConsumerId = "teste",
                organisasjonsnummer = "990983666"
            )
        }
        assertNotNull(organisasjon)
    }

    @Test
    fun `Skal slippe igjennom feilkoder fra Ereg`() {
        val eregService = mockEregService("ereg_notfound.json".asResource().readText(), HttpStatusCode.NotFound)
        val exception = assertThrows<EregException> {
            runBlocking {
                eregService.organisasjon(
                    navCallId = "TEST",
                    navConsumerId = "teste",
                    organisasjonsnummer = "990983666"
                )
            }
        }

        assertEquals(HttpStatusCode.NotFound, exception.errorCode)
        assertEquals("{\n  \"melding\": \"Virksomhet ikke funnet\"\n}", exception.message)
    }

    private fun mockEregService(testResponseString: String, statusCode: HttpStatusCode = HttpStatusCode.OK) =
        EregService(eregEndpoint, setupMockEngine(testResponseString, statusCode))

    private fun setupMockEngine(
        testResponseString: String,
        statusCode: HttpStatusCode = HttpStatusCode.OK
    ) = HttpClient(MockEngine {
        respond(
            content = testResponseString,
            headers = headersOf("Content-Type", ContentType.Application.Json.toString()),
            status = statusCode
        )
    }) {
        expectSuccess = false
        install(JsonFeature, jsonClientConfiguration)
    }

}