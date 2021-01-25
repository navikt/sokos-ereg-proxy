package no.nav.sokos.ereg.proxy.ereg.entities

import kotlinx.serialization.Serializable
import no.nav.sokos.ereg.proxy.ereg.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class OrganisasjonDetaljer(
    @Serializable(with = LocalDateTimeSerializer::class)
    val registreringsdato: LocalDateTime? = null,
    val forretningsadresser: List<Adresse>? = null,
    val postadresser: List<Adresse>? = null,
)