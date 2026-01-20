package no.nav.sokos.ereg.proxy

object TestUtil {
    fun String.readFromResource(): String {
        val clazz = {}::class.java.classLoader
        val resource = clazz.getResource(this)
        requireNotNull(resource) { "Resource $this not found." }
        return resource.readText()
    }
}
