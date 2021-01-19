package no.nav.sokos.ereg.proxy.ereg

import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.features.NotFoundException
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import no.nav.sokos.ereg.proxy.defaultHttpClient
import no.nav.sokos.ereg.proxy.metrics.Metrics
import no.nav.sokos.ereg.proxy.ereg.entities.Organisasjon
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
                when (response.status.value) {
                    200 -> validateEregResponse { response.receive() }
                    404 -> throw NotFoundException()
                    else -> {
                        throw Exception(
                            "$navCallId - Ereg returnerte statuskode ${response.status.value} " +
                                "med melding: \"${response.receive<JsonElement>().jsonObject["melding"]}\"")
                    }
                }
            },
            onFailure = { ex ->
                LOGGER.error("$navCallId - Feil oppstått ved kall til Ereg: ${ex.localizedMessage}")
                throw Exception(ex)
            }
        )
    }

    private inline fun validateEregResponse(block: () -> Organisasjon): Organisasjon {
        // TODO Implement
        return try {
            block()
        } catch (e: Throwable) {
            Metrics.eregValidationErrorCounter.inc()
            LOGGER.error("Feil ved mapping av Ereg-json.", e)
            throw e
        }
    }
}