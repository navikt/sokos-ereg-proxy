package no.nav.sokos.ereg.proxy.ereg.entities

/**
 * Informasjon om organisasjonsnavn
 *
 * @param bruksperiode
 * @param gyldighetsperiode
 * @param navnelinje1 Navnelinje #1
 * @param navnelinje2 Navnelinje #2
 * @param navnelinje3 Navnelinje #3
 * @param navnelinje4 Navnelinje #4
 * @param navnelinje5 Navnelinje #5
 * @param redigertnavn Redigert navn
 */
data class Navn(
    val bruksperiode: Bruksperiode? = null,
    val gyldighetsperiode: Gyldighetsperiode? = null,
    val navnelinje1: String? = null,
    val navnelinje2: String? = null,
    val navnelinje3: String? = null,
    val navnelinje4: String? = null,
    val navnelinje5: String? = null,
    val redigertnavn: String? = null
)