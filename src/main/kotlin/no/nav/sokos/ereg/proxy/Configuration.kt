package no.nav.sokos.ereg.proxy

data class Configuration (
        val eregHost: String = readProperty("EREG_HOSTNAME"),
)

fun readProperty(key: String): String {
    return System.getProperty(key) ?: System.getenv(key) ?: throw RuntimeException("Property $key not found")
}
