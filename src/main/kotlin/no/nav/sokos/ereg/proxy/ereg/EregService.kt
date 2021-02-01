package no.nav.sokos.ereg.proxy.ereg

import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode.Companion.OK
import kotlinx.coroutines.delay
import no.nav.sokos.ereg.proxy.defaultHttpClient
import no.nav.sokos.ereg.proxy.ereg.entities.Organisasjon
import no.nav.sokos.ereg.proxy.metrics.Metrics
import org.slf4j.LoggerFactory

private val LOGGER = LoggerFactory.getLogger("no.nav.sokos.ereg.proxy.ereg.EregService")


class EregService(
    private val eregEndpoint: String,
    private val httpClient: HttpClient = defaultHttpClient
) {
    private val delayMillis = 200L

    suspend fun organisasjon(
        navCallId: String,
        navConsumerId: String,
        organisasjonsnummer: String,
        inkluderHierarki: Boolean = false,
        inkluderHistorikk: Boolean = false
    ): Organisasjon = retry {
        httpClient.get<HttpResponse> {
            header("Nav-Call-Id", navCallId)
            header("Nav-Consumer-Id", navConsumerId)
            parameter("inkluderHistorikk", inkluderHistorikk)
            parameter("inkluderHierarki", inkluderHierarki)
            url("$eregEndpoint/$organisasjonsnummer")
        }
    }.let { response ->
        Metrics.eregCallCounter.labels("${response.status.value}").inc()
        when (response.status) {
            OK -> response.receive()
            else -> throw EregException(
                message = response.readText(),
                errorCode = response.status
            )
        }
    }

    private suspend fun <T> retry(numOfRetries: Int = 5, block: suspend () -> T): T {
        var throwable: Throwable? = null
        (1..numOfRetries).forEach { _ ->
            try {
                return block()
            } catch (ex: Exception) {
                throwable = ex
                delay(delayMillis)
            }
        }
        throw throwable!!
    }
}