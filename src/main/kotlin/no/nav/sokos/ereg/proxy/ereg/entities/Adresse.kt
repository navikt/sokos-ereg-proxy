package no.nav.sokos.ereg.proxy.ereg.entities

import kotlinx.serialization.Serializable

@Serializable
data class Adresse(
    val adresselinje1: String? = null,
    val adresselinje2: String? = null,
    val adresselinje3: String? = null,
    val postnummer: String? = null,
    val poststed: String? = null,
    val landkode : String? = null,
    val kommunenummer: String? = null,
    val bruksperiode: Bruksperiode? = null,
    val gyldighetsperiode: Gyldighetsperiode? = null,
)

