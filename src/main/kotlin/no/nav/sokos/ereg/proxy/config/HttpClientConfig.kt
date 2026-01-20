package no.nav.sokos.ereg.proxy.config

import java.net.ProxySelector

import kotlinx.serialization.json.Json

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import org.apache.http.impl.conn.SystemDefaultRoutePlanner

val json =
    Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
    }

val httpClient =
    HttpClient(Apache) {
        expectSuccess = false

        install(ContentNegotiation) {
            json(json)
        }

        engine {
            customizeClient {
                setRoutePlanner(SystemDefaultRoutePlanner(ProxySelector.getDefault()))
            }
        }
    }
