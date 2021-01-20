package no.nav.sokos.ereg.proxy.api

data class OrganisasjonInfo(
    val organisasjonsnummer: String,
    val organisasjonstype: String,
    val navn: Navn,
    val forretningsadresse: Adresse?,
    val postadresse: Adresse?
)

data class Navn(
    val navnelinje1: String?,
    val navnelinje2: String?,
    val navnelinje3: String?,
    val navnelinje4: String?,
    val navnelinje5: String?,
    val redigertnavn: String?
)

data class Adresse(
    val adresselinje1: String?,
    val adresselinje2: String?,
    val adresselinje3: String?,
    val kommunenummer: String?,
    val landkode: String?,
    val postnummer: String?,
    val poststed: String?
)

data class TjenestefeilResponse(
    val melding: String
)
