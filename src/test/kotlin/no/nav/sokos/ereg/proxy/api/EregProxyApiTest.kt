package no.nav.sokos.ereg.proxy.api

import kotlinx.serialization.json.Json

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.routing.routing
import io.mockk.coEvery
import io.mockk.mockk
import io.restassured.RestAssured

import no.nav.sokos.ereg.proxy.TestUtil.readFromResource
import no.nav.sokos.ereg.proxy.config.commonConfig
import no.nav.sokos.ereg.proxy.config.json
import no.nav.sokos.ereg.proxy.ereg.EregClientService
import no.nav.sokos.ereg.proxy.ereg.EregException
import no.nav.sokos.ereg.proxy.ereg.entities.Organisasjon

private const val PORT = 9090

private lateinit var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>

private val validationFilter = OpenApiValidationFilter("openapi/ereg-proxy_v1_002.json")
private val eregClientService = mockk<EregClientService>()

private const val ORG_NR = "889640782"
private const val CONSUMER_ID = "132512"
private const val CALL_ID = "1233210"

internal class EregProxyApiTest :
    FunSpec({

        beforeTest {
            server = embeddedServer(Netty, PORT, module = Application::applicationTestModule).start()
        }

        afterTest {
            server.stop(5, 5)
        }

        test("skal returnere 200 OK") {

            val organisasjon = json.decodeFromString<Organisasjon>("ereg_response.json".readFromResource())

            coEvery {
                eregClientService.hentOrganisasjon(any(), any(), any())
            } returns organisasjon

            val response =
                RestAssured
                    .given()
                    .filter(validationFilter)
                    .header("Nav-Call-Id", CALL_ID)
                    .header("Nav-Consumer-Id", CONSUMER_ID)
                    .port(PORT)
                    .get("/organisasjon-proxy/api/v1/organisasjon/$ORG_NR")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatusCode.OK.value)
                    .extract()
                    .response()

            response.statusCode shouldBe HttpStatusCode.OK.value
            response.contentType shouldBe ContentType.Application.Json.toString()

            val expectedResponse =
                """
                {
                  "organisasjonsnummer": "990983666",
                  "organisasjonstype": "Virksomhet",
                  "navn": {
                    "navnelinje1": "NAV IKT",
                    "redigertnavn": "NAV IKT"
                  },
                  "forretningsadresse": {
                    "adresselinje1": "Sannergata 2",
                    "kommunenummer": "0301",
                    "landkode": "NO",
                    "postnummer": "0557"
                  },
                  "postadresse": {
                    "adresselinje1": "Postboks 5 St Olavs plass",
                    "kommunenummer": "0301",
                    "landkode": "NO",
                    "postnummer": "0130"
                  }
                }
                """.trimIndent()

            Json.parseToJsonElement(response.asString()) shouldBe Json.parseToJsonElement(expectedResponse)
        }

        test("Skal slippe gjennom feilmeldinger fra Ereg") {

            coEvery {
                eregClientService.hentOrganisasjon(any(), any(), any())
            } throws
                EregException(
                    """
                    {
                      "melding" : "Virksomhet ikke funnet"
                    }
                    """.trimIndent(),
                    HttpStatusCode.NotFound,
                )

            val response =
                RestAssured
                    .given()
                    .filter(validationFilter)
                    .header("Nav-Call-Id", CALL_ID)
                    .header("Nav-Consumer-Id", CONSUMER_ID)
                    .port(PORT)
                    .get("/organisasjon-proxy/api/v1/organisasjon/$ORG_NR")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatusCode.NotFound.value)
                    .extract()
                    .response()

            response.statusCode shouldBe HttpStatusCode.NotFound.value
            response.contentType shouldBe ContentType.Application.Json.toString()
            val expectedResponse =
                """
                {
                  "melding" : "Virksomhet ikke funnet"
                }
                """.trimIndent()

            Json.parseToJsonElement(response.asString()) shouldBe Json.parseToJsonElement(expectedResponse)
        }

        test("Skal returnere internal server error og feilmelding ved feil") {

            coEvery {
                eregClientService.hentOrganisasjon(any(), any(), any())
            } throws
                Exception(
                    """
                    {
                      "melding" : "Virksomhet ikke funnet"
                    }
                    """.trimIndent(),
                )

            val response =
                RestAssured
                    .given()
                    .filter(validationFilter)
                    .header("Nav-Call-Id", CALL_ID)
                    .header("Nav-Consumer-Id", CONSUMER_ID)
                    .port(PORT)
                    .get("/organisasjon-proxy/api/v1/organisasjon/$ORG_NR")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatusCode.InternalServerError.value)
                    .extract()
                    .response()

            response.statusCode shouldBe HttpStatusCode.InternalServerError.value
            response.contentType shouldBe ContentType.Application.Json.toString()

            val expectedResponse =
                """
                {
                  "melding" : "Det har oppstått en feil. Se log for feilmelding. (x-correlation-id: 1233210)"
                }
                """.trimIndent()

            Json.parseToJsonElement(response.asString()) shouldBe Json.parseToJsonElement(expectedResponse)
        }
    })

private fun Application.applicationTestModule() {
    commonConfig()
    routing {
        eregProxyApi(eregClientService)
    }
}
