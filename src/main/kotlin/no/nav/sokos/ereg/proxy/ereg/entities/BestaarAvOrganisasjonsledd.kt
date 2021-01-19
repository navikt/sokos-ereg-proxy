package no.nav.sokos.ereg.proxy.ereg.entities

import kotlinx.serialization.Serializable
import no.nav.sokos.ereg.proxy.ereg.entities.Bruksperiode
import no.nav.sokos.ereg.proxy.ereg.entities.Gyldighetsperiode
import no.nav.sokos.ereg.proxy.ereg.entities.Organisasjon

/**
 * Best&aring;r av organisasjonsledd
 *
 * @param bruksperiode
 * @param gyldighetsperiode 
 * @param organisasjonsledd 
 */
@Serializable
data class BestaarAvOrganisasjonsledd (
    val bruksperiode: Bruksperiode? = null,
    val gyldighetsperiode: Gyldighetsperiode? = null,
    val organisasjonsledd: Organisasjon? = null
)