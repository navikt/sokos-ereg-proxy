package no.nav.sokos.ereg.proxy

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.sokos.ereg.proxy.ereg.entities.Organisasjon
import java.net.URL

private fun String.asResource(): URL = {}::class.java.classLoader.getResource(this)!!

fun resourceToString(filename: String) = filename.asResource().readText()

fun String.parseVirksomhet(): Organisasjon = jsonMapper.readValue(asResource())
