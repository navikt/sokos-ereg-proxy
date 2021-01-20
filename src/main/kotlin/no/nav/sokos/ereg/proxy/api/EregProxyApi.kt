package no.nav.sokos.ereg.proxy.api

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.util.getOrFail
import no.nav.sokos.ereg.proxy.ereg.EregException
import no.nav.sokos.ereg.proxy.ereg.EregService
import no.nav.sokos.ereg.proxy.ereg.entities.Organisasjon
import org.slf4j.LoggerFactory

private val LOGGER = LoggerFactory.getLogger("no.nav.sokos.ereg.proxy.api.EregProxyApiKt")

fun Application.eregProxyApi(eregService: EregService) {
    routing {
        route("/eregproxy/api/v1") {
            get("/organisasjon/{orgnr}") {
                val organisasjonsnummer = call.request.queryParameters.getOrFail("orgnr")
                val navCallId = call.request.headers["Nav-Call-Id"] ?: ""
                val navConsumerId = call.request.headers["Nav-Consumer-Id"] ?: ""

                try {
                    val org: Organisasjon = eregService.organisasjon(
                        navCallId,
                        navConsumerId,
                        organisasjonsnummer,
                        inkluderHierarki = false
                    )

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
                } catch (e: EregException) {
                    LOGGER.warn("Ereg returnerte ${e.errorCode} med melding ${e.message}")
                    call.respond(e.errorCode, TjenestefeilResponse(e.message))
                }
            }
        }
    }
}

private fun Organisasjon.forretningsadresse() = organisasjonDetaljer?.forretningsadresser?.get(0)
private fun Organisasjon.postadresse() = organisasjonDetaljer?.postadresser?.get(0)
