package no.nav.sokos.ereg.proxy.api

import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationRequest
import io.ktor.server.testing.contentType
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.sokos.ereg.proxy.ereg.EregException
import no.nav.sokos.ereg.proxy.ereg.EregService
import no.nav.sokos.ereg.proxy.parseVirksomhet
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EregProxyApiKtTest {

    private val validOrgnr = "889640782"
    private val consumerId = "132512"
    private val callId = "1233210"

    private val eregMock: EregService = mockk()

    private val callIdHeader: TestApplicationRequest.() -> Unit = {
        addHeader("Nav-Call-Id", callId)
        addHeader("Nav-Consumer-Id", consumerId)
    }

    @Test
    fun `skal returnere 200 OK`() {
        coEvery {
            eregMock.organisasjon(callId, consumerId, validOrgnr, false, false)
        } returns "ereg_response1.json".parseVirksomhet()

        val testCall: TestApplicationCall = withTestApplication({
            commonFeatures()
            eregProxyApi(eregMock)
        }) {
            handleRequest(
                method = HttpMethod.Get,
                uri = "/organisasjon-proxy/api/v1/organisasjon/$validOrgnr",
                setup = callIdHeader,
            )
        }
        val response = testCall.response
        assertEquals(HttpStatusCode.OK, response.status())
        assertEquals(callId, response.headers["Nav-Call-Id"])
        assertEquals(consumerId, response.headers["Nav-Consumer-Id"])
        assertEquals(ContentType.Application.Json.contentSubtype, response.contentType().contentSubtype)
        assertEquals(
            """
            {
              "organisasjonsnummer" : "990983666",
              "organisasjonstype" : "Virksomhet",
              "navn" : {
                "navnelinje1" : "NAV IKT",
                "redigertnavn" : "NAV IKT"
              },
              "forretningsadresse" : {
                "adresselinje1" : "Sannergata 2",
                "kommunenummer" : "0301",
                "landkode" : "NO",
                "postnummer" : "0557"
              },
              "postadresse" : {
                "adresselinje1" : "Postboks 5 St Olavs plass",
                "kommunenummer" : "0301",
                "landkode" : "NO",
                "postnummer" : "0130"
              }
            }""".trimIndent(), response.content
        )


    }

    @Test
    fun `Skal slippe gjennom feilmeldinger fra Ereg`() {
        coEvery {
            eregMock.organisasjon(callId, consumerId, validOrgnr, false, false)
        } throws EregException("{\n  \"melding\" : \"Virksomhet ikke funnet\"\n}", HttpStatusCode.NotFound)

        val testCall: TestApplicationCall = withTestApplication({
            commonFeatures()
            eregProxyApi(eregMock)
        }) {
            handleRequest(
                method = HttpMethod.Get,
                uri = "/organisasjon-proxy/api/v1/organisasjon/$validOrgnr",
                setup = callIdHeader,
            )
        }

        testCall.response.let { response ->
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertEquals(callId, response.headers["Nav-Call-Id"])
            assertEquals(consumerId, response.headers["Nav-Consumer-Id"])
            assertEquals(ContentType.Application.Json.contentSubtype, response.contentType().contentSubtype)
            assertEquals("{\n  \"melding\" : \"Virksomhet ikke funnet\"\n}", response.content)
        }
    }

    @Test
    fun `Skal returnere internal server error og feilmelding ved feil`() {
        coEvery {
            eregMock.organisasjon(callId, consumerId, validOrgnr, false, false)
        } throws Exception("{\n  \"melding\" : \"Virksomhet ikke funnet\"\n}")

        val testCall: TestApplicationCall = withTestApplication({
            commonFeatures()
            eregProxyApi(eregMock)
        }) {
            handleRequest(
                method = HttpMethod.Get,
                uri = "/organisasjon-proxy/api/v1/organisasjon/$validOrgnr",
                setup = callIdHeader,
            )
        }

        testCall.response.let { response ->
            assertEquals(HttpStatusCode.InternalServerError, response.status())
            assertEquals(callId, response.headers["Nav-Call-Id"])
            assertEquals(consumerId, response.headers["Nav-Consumer-Id"])
            assertEquals(ContentType.Application.Json.contentSubtype, response.contentType().contentSubtype)
            assertEquals("{\n  \"melding\" : \"Det har oppstått en feil. Se log for feilmelding. (x-correlation-id: 1233210)\"\n}", response.content)
        }
    }
}

