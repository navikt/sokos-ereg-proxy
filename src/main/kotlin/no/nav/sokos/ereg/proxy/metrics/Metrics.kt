package no.nav.sokos.ereg.proxy.metrics

import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import io.prometheus.metrics.core.metrics.Counter

private const val METRICS_NAMESPACE = "sokos_ereg_proxy"

private const val EREG_CALL_COUNTER = "${METRICS_NAMESPACE}_ereg_call_counter"

object Metrics {
    val prometheusMeterRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    val eregCallCounter: Counter =
        Counter
            .builder()
            .name(EREG_CALL_COUNTER)
            .help("Counts calls to Ereg with response status code")
            .withoutExemplars()
            .labelNames("responseCode")
            .register(prometheusMeterRegistry.prometheusRegistry)
}
