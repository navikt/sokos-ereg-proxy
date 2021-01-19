package no.nav.sokos.ereg.proxy.ereg.entities

import kotlinx.serialization.Serializable

/**
 * Inng&aring;r i juridisk enhet
 * @param organisasjonsnummer Organisasjonsnummer
 * @param bruksperiode
 * @param gyldighetsperiode 
 * @param navn Informasjon om organisasjonsnavn
 */
@Serializable
data class InngaarIJuridiskEnhet (
    val organisasjonsnummer: String,
    val navn: Navn? = null,
    val bruksperiode: Bruksperiode? = null,
    val gyldighetsperiode: Gyldighetsperiode? = null
)