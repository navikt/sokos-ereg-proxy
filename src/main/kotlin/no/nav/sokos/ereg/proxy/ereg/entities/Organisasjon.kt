package no.nav.sokos.ereg.proxy.ereg.entities

/**
 * Informasjon om virksomhet
 *
 * @param navn Informasjon om organisasjonsnavn
 * @param organisasjonsnummer Organisasjonsnummer
 * @param type Organisasjonstype: Virksomhet/JuridiskEnhet/Organisasjonsledd
 * @param inngaarIJuridiskEnheter Liste av hvilke(n) juridisk enhet virksomhet inng&aring;r i
 * @param bestaarAvOrganisasjonsledd Liste av hvilke organisasjonsledd virksomhet best&aring;r av
 */
data class Organisasjon(
    val navn: Navn,
    val organisasjonsnummer: String,
    val type: String,
    val inngaarIJuridiskEnheter: List<InngaarIJuridiskEnhet>? = null,
    val bestaarAvOrganisasjonsledd: List<BestaarAvOrganisasjonsledd>? = null,
    val organisasjonsleddOver: List<BestaarAvOrganisasjonsledd>? = null,
    val organisasjonsleddUnder: List<BestaarAvOrganisasjonsledd>? = null,
    val organisasjonDetaljer: OrganisasjonDetaljer? = null,
)
