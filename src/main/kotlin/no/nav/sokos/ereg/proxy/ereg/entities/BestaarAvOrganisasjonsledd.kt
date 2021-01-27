package no.nav.sokos.ereg.proxy.ereg.entities

/**
 * Best&aring;r av organisasjonsledd
 *
 * @param bruksperiode
 * @param gyldighetsperiode 
 * @param organisasjonsledd 
 */
data class BestaarAvOrganisasjonsledd (
    val bruksperiode: Bruksperiode? = null,
    val gyldighetsperiode: Gyldighetsperiode? = null,
    val organisasjonsledd: Organisasjon? = null
)