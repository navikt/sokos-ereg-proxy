package no.nav.sokos.ereg.proxy.api

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallId
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.callIdMdc
import io.ktor.jackson.jackson
import io.ktor.request.path
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.util.UUID

private val LOGGER = LoggerFactory.getLogger("no.nav.sokos.ereg.proxy.api.HttpServer")

fun Application.installCommonFeatures(){
    install(CallId) {
        header("Nav-Call-Id")
        generate { UUID.randomUUID().toString() }
        verify { it.isNotEmpty() }
    }
    install(CallLogging) {
        logger = LOGGER
        level = Level.INFO
        callIdMdc("x-correlation-id")
        filter { call -> call.request.path().startsWith("/eregproxy") }
    }
    install(ContentNegotiation) {
        jackson {
            registerKotlinModule()
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
}
