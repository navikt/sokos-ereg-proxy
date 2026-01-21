package no.nav.sokos.ereg.proxy.ereg

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

import no.nav.sokos.ereg.proxy.config.PropertiesConfig
import no.nav.sokos.ereg.proxy.config.httpClient
import no.nav.sokos.ereg.proxy.ereg.entities.Organisasjon
import no.nav.sokos.ereg.proxy.metrics.Metrics

class EregClientService(
    private val eregUrl: String = PropertiesConfig.Ereg().eregUrl,
    private val client: HttpClient = httpClient,
) {
    suspend fun hentOrganisasjon(
        organisasjonsnummer: String,
        inkluderHierarki: Boolean = false,
        inkluderHistorikk: Boolean = false,
    ): Organisasjon {
        val response =
            client.get("$eregUrl/ereg/api/v1/organisasjon/$organisasjonsnummer") {
                parameter("inkluderHistorikk", inkluderHistorikk)
                parameter("inkluderHierarki", inkluderHierarki)
            }

        Metrics.eregCallCounter.labelValues("${response.status.value}").inc()

        return when {
            response.status.isSuccess() -> response.body<Organisasjon>()
            else -> {
                throw EregException(
                    message = Json.decodeFromString<ErrorResponse>(response.bodyAsText()).melding,
                    errorCode = response.status,
                )
            }
        }
    }
}

@Serializable
data class ErrorResponse(
    val melding: String,
)
