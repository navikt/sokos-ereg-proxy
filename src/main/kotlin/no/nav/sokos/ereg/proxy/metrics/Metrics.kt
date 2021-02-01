package no.nav.sokos.ereg.proxy.metrics

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.http.ContentType
import io.ktor.metrics.micrometer.MicrometerMetrics
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.micrometer.core.instrument.binder.system.UptimeMetrics
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.prometheus.client.Counter
import io.prometheus.client.Summary
import io.prometheus.client.exporter.common.TextFormat

private const val NAMESPACE = "sokos_ereg_proxy"

object Metrics {
    val registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    val appStateRunningFalse: Counter = Counter.build()
        .namespace(NAMESPACE)
        .name("app_state_running_false")
        .help("app state running changed to false")
        .register(registry.prometheusRegistry)

    val appStateReadyFalse: Counter = Counter.build()
        .namespace(NAMESPACE)
        .name("app_state_ready_false")
        .help("app state ready changed to false")
        .register(registry.prometheusRegistry)

    val eregCallCounter: Counter = Counter.build()
        .namespace(NAMESPACE)
        .name("ereg_call_counter")
        .labelNames("responseCode")
        .help("Counts calls to ereg with response status code")
        .register(registry.prometheusRegistry)

    val eregValidationErrorCounter: Counter = Counter.build()
        .namespace(NAMESPACE)
        .name("ereg_validation_error_counter")
        .help("Counts validating errors in ereg response")
        .register(registry.prometheusRegistry)

    val eregCallSummary: Summary = Summary.build()
        .namespace(NAMESPACE)
        .name("ereg_call_summary")
        .help("Ereg kall timer")
        .register(registry.prometheusRegistry)

    val eregMappingSummary: Summary = Summary.build()
        .namespace(NAMESPACE)
        .name("ereg_mapping_summary")
        .help("Ereg mapping timer")
        .register(registry.prometheusRegistry)

    val serviceFaultCounter: Counter = Counter.build()
        .namespace(NAMESPACE)
        .name("service_fault_counter")
        .labelNames("exception")
        .help("Errors occurred in organisasjon API call")
        .register(registry.prometheusRegistry)
}

fun Application.metrics() {
    install(MicrometerMetrics) {
        registry = Metrics.registry
        meterBinders = listOf(
            UptimeMetrics()
        )
    }
    routing {
        route("metrics") {
            get {
                call.respondText(ContentType.parse(TextFormat.CONTENT_TYPE_004)) { Metrics.registry.scrape() }
            }
        }
    }
}