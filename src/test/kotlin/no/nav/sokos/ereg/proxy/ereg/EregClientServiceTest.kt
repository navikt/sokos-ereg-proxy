package no.nav.sokos.ereg.proxy.ereg

import kotlin.getValue
import kotlin.test.assertNotNull

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.okJson
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import org.junit.jupiter.api.assertThrows

import no.nav.sokos.ereg.proxy.TestUtil.readFromResource
import no.nav.sokos.ereg.proxy.listener.WiremockListener
import no.nav.sokos.ereg.proxy.listener.WiremockListener.wiremock

private const val ORG_NNUMMER = "889640782"

internal class EregClientServiceTest :
    FunSpec({

        extensions(listOf(WiremockListener))

        val eregClientService: EregClientService by lazy {
            EregClientService(
                eregUrl = wiremock.baseUrl(),
            )
        }

        test("Skal mappe organisasjon fra Ereg") {

            val eregResponse = "ereg_response.json".readFromResource()

            wiremock.stubFor(
                get(urlPathEqualTo("/ereg/api/v1/organisasjon/$ORG_NNUMMER"))
                    .withQueryParam("inkluderHistorikk", equalTo("false"))
                    .withQueryParam("inkluderHierarki", equalTo("false"))
                    .willReturn(
                        okJson(eregResponse),
                    ),
            )

            val response =
                eregClientService.hentOrganisasjon(
                    navCallId = "callId",
                    navConsumerId = "navConsumerId",
                    organisasjonsnummer = ORG_NNUMMER,
                )

            assertNotNull(response)
        }

        test("Skal slippe igjennom feilkoder fra Ereg") {

            val eregResponse = "ereg_notfound.json".readFromResource()

            wiremock.stubFor(
                get(urlPathEqualTo("/ereg/api/v1/organisasjon/$ORG_NNUMMER"))
                    .withQueryParam("inkluderHistorikk", equalTo("false"))
                    .withQueryParam("inkluderHierarki", equalTo("false"))
                    .willReturn(
                        aResponse()
                            .withStatus(404)
                            .withBody(eregResponse),
                    ),
            )

            val exception =
                assertThrows<EregException> {
                    eregClientService.hentOrganisasjon(
                        navCallId = "TEST",
                        navConsumerId = "teste",
                        organisasjonsnummer = ORG_NNUMMER,
                    )
                }

            exception.errorCode shouldBe HttpStatusCode.NotFound
            "Virksomhet ikke funnet" shouldBe exception.message
        }
    })
