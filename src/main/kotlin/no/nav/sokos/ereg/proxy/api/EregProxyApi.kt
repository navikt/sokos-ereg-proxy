package no.nav.sokos.ereg.proxy.api

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.util.getOrFail
import io.prometheus.client.Summary
import no.nav.sokos.ereg.proxy.ereg.EregException
import no.nav.sokos.ereg.proxy.ereg.EregService
import no.nav.sokos.ereg.proxy.ereg.entities.Organisasjon
import no.nav.sokos.ereg.proxy.metrics.Metrics
import org.slf4j.LoggerFactory

private val LOGGER = LoggerFactory.getLogger("no.nav.sokos.ereg.proxy.api.EregProxyApiKt")

fun Application.eregProxyApi(eregService: EregService) {
    routing {
        route("organisasjon-proxy/api") {
            get("v1/organisasjon/{orgnr}") {
                val organisasjonsnummer = call.parameters.getOrFail("orgnr")
                val navCallId = call.request.headers["Nav-Call-Id"] ?: ""
                val navConsumerId = call.request.headers["Nav-Consumer-Id"] ?: ""

                call.response.header("Nav-Consumer-Id", navConsumerId)

                try {
                    val org: Organisasjon = Metrics.eregCallSummary.cotime {
                        eregService.organisasjon(
                            navCallId,
                            navConsumerId,
                            organisasjonsnummer
                        )
                    }
                    Metrics.eregMappingSummary.cotime {
                        call.respond(OK, OrganisasjonInfo(
                            organisasjonsnummer = org.organisasjonsnummer,
                            organisasjonstype = org.type,
                            navn = Navn(
                                navnelinje1 = org.navn.navnelinje1,
                                navnelinje2 = org.navn.navnelinje2,
                                navnelinje3 = org.navn.navnelinje3,
                                navnelinje4 = org.navn.navnelinje4,
                                navnelinje5 = org.navn.navnelinje5,
                                redigertnavn = org.navn.redigertnavn
                            ),
                            forretningsadresse = org.forretningsadresse()?.let {
                                Adresse(
                                    adresselinje1 = it.adresselinje1,
                                    adresselinje2 = it.adresselinje2,
                                    adresselinje3 = it.adresselinje3,
                                    kommunenummer = it.kommunenummer,
                                    landkode = it.landkode,
                                    postnummer = it.postnummer,
                                    poststed = it.poststed
                                )
                            },
                            postadresse = org.postadresse()?.let {
                                Adresse(
                                    adresselinje1 = it.adresselinje1,
                                    adresselinje2 = it.adresselinje2,
                                    adresselinje3 = it.adresselinje3,
                                    kommunenummer = it.kommunenummer,
                                    landkode = it.landkode,
                                    postnummer = it.postnummer,
                                    poststed = it.poststed
                                )
                            }
                        ))
                    }
                } catch (e: EregException) {
                    LOGGER.warn("Ereg returnerte ${e.errorCode} med melding ${e.message}")
                    call.respondText(
                        text = e.message,
                        contentType = ContentType.Application.Json,
                        status = e.errorCode
                    )
                }  catch (ex: Exception) {
                    LOGGER.error("Det har oppstått en feil.", ex)
                    Metrics.serviceFaultCounter.labels(ex::class.java.canonicalName).inc()
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        TjenestefeilResponse(
                            "Det har oppstått en feil. Se log for feilmelding. (x-correlation-id: $navCallId)")
                    )
                }
            }
        }
    }
}

private fun Organisasjon.forretningsadresse() = organisasjonDetaljer?.forretningsadresser?.get(0)
private fun Organisasjon.postadresse() = organisasjonDetaljer?.postadresser?.get(0)

private inline fun <T> Summary.cotime(block: () -> T): T {
    val timer = startTimer()
    val result = block()
    timer.observeDuration()
    return result
}
