package no.nav.sokos.ereg.proxy.ereg.entities

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * Bruksperiode
 *
 * @param fom Fra-dato for bruksperiode, format (ISO-8601): yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS]]
 * @param tom Til-dato for bruksperiode, format (ISO-8601): yyyy-MM-dd'T'HH:mm[:ss[.SSSSSSSSS]]
 */

@Serializable
data class Bruksperiode(
    val fom: LocalDateTime? = null,
    val tom: LocalDateTime? = null,
)
