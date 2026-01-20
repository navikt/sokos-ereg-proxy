package no.nav.sokos.ereg.proxy.ereg.entities

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class OrganisasjonDetaljer(
    val registreringsdato: LocalDateTime? = null,
    val forretningsadresser: List<Adresse>? = null,
    val postadresser: List<Adresse>? = null,
)
