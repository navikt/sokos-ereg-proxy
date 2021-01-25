package no.nav.sokos.ereg.proxy.ereg

import io.ktor.http.HttpStatusCode

class EregException(
    override val message: String,
    val errorCode: HttpStatusCode,
): Exception(message)