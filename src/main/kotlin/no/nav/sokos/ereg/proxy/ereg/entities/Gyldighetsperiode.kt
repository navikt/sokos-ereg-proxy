package no.nav.sokos.ereg.proxy.ereg.entities

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

/**
 * Gyldighetsperiode
 *
 * @param fom Fra-og-med-dato for gyldighetsperiode, format (ISO-8601): yyyy-MM-dd
 * @param tom Til-og-med-dato for gyldighetsperiode, format (ISO-8601): yyyy-MM-dd
 */

@Serializable
data class Gyldighetsperiode(
    val fom: LocalDate? = null,
    val tom: LocalDate? = null,
)
