package no.nav.sokos.ereg.proxy.ereg.entities

import kotlinx.serialization.Serializable
import no.nav.sokos.ereg.proxy.ereg.LocalDateSerializer
import java.time.LocalDate

/**
 * Gyldighetsperiode
 *
 * @param fom Fra-og-med-dato for gyldighetsperiode, format (ISO-8601): yyyy-MM-dd
 * @param tom Til-og-med-dato for gyldighetsperiode, format (ISO-8601): yyyy-MM-dd
 */
@Serializable
data class Gyldighetsperiode(
    @Serializable(with = LocalDateSerializer::class)
    val fom: LocalDate? = null,
    @Serializable(with = LocalDateSerializer::class)
    val tom: LocalDate? = null
)