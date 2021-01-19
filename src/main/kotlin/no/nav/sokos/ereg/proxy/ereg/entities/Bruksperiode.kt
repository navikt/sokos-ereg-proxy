package no.nav.sokos.ereg.proxy.ereg.entities

import kotlinx.serialization.Serializable
import no.nav.sokos.ereg.proxy.ereg.LocalDateTimeSerializer
import java.time.LocalDateTime

/**
 * Bruksperiode
 *
 * @param fom Fra-dato for bruksperiode, format (ISO-8601): yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS]]
 * @param tom Til-dato for bruksperiode, format (ISO-8601): yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS]]
 */
@Serializable
data class Bruksperiode (
    @Serializable(with = LocalDateTimeSerializer::class)
    val fom: LocalDateTime? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val tom: LocalDateTime? = null
)