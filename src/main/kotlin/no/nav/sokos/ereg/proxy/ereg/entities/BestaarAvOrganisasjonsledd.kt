package no.nav.sokos.ereg.proxy.ereg.entities

import kotlinx.serialization.Serializable

/**
 * Best&aring;r av organisasjonsledd
 *
 * @param bruksperiode
 * @param gyldighetsperiode
 * @param organisasjonsledd
 */

@Serializable
data class BestaarAvOrganisasjonsledd(
    val bruksperiode: Bruksperiode? = null,
    val gyldighetsperiode: Gyldighetsperiode? = null,
    val organisasjonsledd: Organisasjon? = null,
)
