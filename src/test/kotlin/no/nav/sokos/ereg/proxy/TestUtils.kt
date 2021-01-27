package no.nav.sokos.ereg.proxy

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.sokos.ereg.proxy.ereg.entities.Organisasjon
import java.net.URL

fun String.asResource(): URL = object {}.javaClass.classLoader.getResource(this)!!

fun String.parseVirksomhet(): Organisasjon = jsonMapper.readValue(asResource())
