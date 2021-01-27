package no.nav.sokos.ereg.proxy.ereg.entities

import java.time.LocalDate

/**
 * Gyldighetsperiode
 *
 * @param fom Fra-og-med-dato for gyldighetsperiode, format (ISO-8601): yyyy-MM-dd
 * @param tom Til-og-med-dato for gyldighetsperiode, format (ISO-8601): yyyy-MM-dd
 */
data class Gyldighetsperiode(
    val fom: LocalDate? = null,
    val tom: LocalDate? = null
)