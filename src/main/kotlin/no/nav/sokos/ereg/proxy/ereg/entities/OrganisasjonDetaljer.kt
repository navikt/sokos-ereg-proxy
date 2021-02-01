package no.nav.sokos.ereg.proxy.ereg.entities

import java.time.LocalDateTime

data class OrganisasjonDetaljer(
    val registreringsdato: LocalDateTime? = null,
    val forretningsadresser: List<Adresse>? = null,
    val postadresser: List<Adresse>? = null,
)