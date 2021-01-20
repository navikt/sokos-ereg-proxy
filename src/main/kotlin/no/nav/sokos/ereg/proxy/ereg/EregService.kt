package no.nav.sokos.ereg.proxy.ereg

import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode.Companion.OK
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import no.nav.sokos.ereg.proxy.defaultHttpClient
import no.nav.sokos.ereg.proxy.ereg.entities.Organisasjon
import no.nav.sokos.ereg.proxy.metrics.Metrics
import org.slf4j.LoggerFactory

private val LOGGER = LoggerFactory.getLogger("no.nav.sokos.ereg.proxy.ereg.EregService")

class EregService(
    private val eregEndpoint: String,
    private val httpClient: HttpClient = defaultHttpClient
) {
    suspend fun organisasjon(
        navCallId: String,
        navConsumerId: String,
        organisasjonsnummer: String,
        inkluderHierarki: Boolean = true,
        inkluderHistorikk: Boolean = false
    ): Organisasjon {
        return runCatching {
            httpClient.get<HttpResponse> {
                header("Nav-Call-Id", navCallId)
                header("Nav-Consumer-Id", navConsumerId)
                parameter("inkluderHistorikk", inkluderHistorikk)
                parameter("inkluderHierarki", inkluderHierarki)
                url("$eregEndpoint/$organisasjonsnummer")
            }
        }.fold(
            onSuccess = { response ->
                Metrics.eregCallCounter.labels("${response.status.value}").inc()
                when (response.status) {
                    OK -> validateEregResponse { response.receive() }
                    else -> throw EregException(
                        message = response.receive<JsonElement>().jsonObject["melding"].toString(),
                        errorCode = response.status
                    )
                }
            },
            onFailure = { ex ->
                LOGGER.error("$navCallId - Feil oppstått ved kall til Ereg: ${ex.localizedMessage}")
                throw Exception(ex)
            }
        )
    }

    private inline fun validateEregResponse(block: () -> Organisasjon): Organisasjon {
        return try {
            block()
        } catch (e: Throwable) {
            Metrics.eregValidationErrorCounter.inc()
            LOGGER.error("Feil ved mapping av Ereg-json.", e)
            throw e
        }
    }
}