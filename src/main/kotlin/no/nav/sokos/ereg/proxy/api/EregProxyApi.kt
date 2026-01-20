package no.nav.sokos.ereg.proxy.api

import kotlinx.serialization.Serializable

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import mu.KotlinLogging

import no.nav.sokos.ereg.proxy.config.TEAM_LOGS_MARKER
import no.nav.sokos.ereg.proxy.ereg.EregClientService
import no.nav.sokos.ereg.proxy.ereg.EregException
import no.nav.sokos.ereg.proxy.ereg.entities.Organisasjon

private val logger = KotlinLogging.logger {}

fun Route.eregProxyApi(eregClientService: EregClientService = EregClientService()) {
    route("organisasjon-proxy/api") {
        get("v1/organisasjon/{orgnr}") {
            val organisasjonsnummer = call.parameters.getOrFail("orgnr")

            try {
                val org =
                    eregClientService.hentOrganisasjon(
                        organisasjonsnummer,
                    )
                val response = mapOrganisasjonToResponse(org)
                call.respond(
                    response,
                )
            } catch (eregException: EregException) {
                logger.info(marker = TEAM_LOGS_MARKER) { eregException.message }
                call.respondText(
                    text = eregException.message,
                    contentType = ContentType.Application.Json,
                    status = eregException.errorCode,
                )
            } catch (exception: Exception) {
                logger.error(marker = TEAM_LOGS_MARKER, exception) { "Det har oppstått en feil." }
                call.respond(
                    HttpStatusCode.InternalServerError,
                    TjenestefeilResponse(
                        "Det har oppstått en feil. Se log for feilmelding.",
                    ),
                )
            }
        }
    }
}

fun mapOrganisasjonToResponse(organisasjon: Organisasjon): OrganisasjonInfoResponse =
    OrganisasjonInfoResponse(
        organisasjonsnummer = organisasjon.organisasjonsnummer,
        organisasjonstype = organisasjon.type,
        navn =
            Navn(
                navnelinje1 = organisasjon.navn.navnelinje1,
                navnelinje2 = organisasjon.navn.navnelinje2,
                navnelinje3 = organisasjon.navn.navnelinje3,
                navnelinje4 = organisasjon.navn.navnelinje4,
                navnelinje5 = organisasjon.navn.navnelinje5,
                redigertnavn = organisasjon.navn.redigertnavn,
            ),
        forretningsadresse =
            organisasjon.forretningsadresse()?.let {
                Adresse(
                    adresselinje1 = it.adresselinje1,
                    adresselinje2 = it.adresselinje2,
                    adresselinje3 = it.adresselinje3,
                    kommunenummer = it.kommunenummer,
                    landkode = it.landkode,
                    postnummer = it.postnummer,
                    poststed = it.poststed,
                )
            },
        postadresse =
            organisasjon.postadresse()?.let {
                Adresse(
                    adresselinje1 = it.adresselinje1,
                    adresselinje2 = it.adresselinje2,
                    adresselinje3 = it.adresselinje3,
                    kommunenummer = it.kommunenummer,
                    landkode = it.landkode,
                    postnummer = it.postnummer,
                    poststed = it.poststed,
                )
            },
    )

private fun Organisasjon.forretningsadresse() = organisasjonDetaljer?.forretningsadresser?.get(0)

private fun Organisasjon.postadresse() = organisasjonDetaljer?.postadresser?.get(0)

@Serializable
data class TjenestefeilResponse(
    val melding: String,
)
