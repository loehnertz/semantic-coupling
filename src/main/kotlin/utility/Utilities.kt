package codes.jakob.semanticcoupling.utility

import java.io.File


object Utilities {
    fun getResourceAsText(path: String): String {
        // return this::class.java.classLoader.getResource(path).readText()  // TODO: Figure out why this is not working
        return File("src/main/resources/$path").readText()
    }

    fun isNonEmptyWordEntry(word: String): Boolean {
        return (word != " " && word != "")
    }
}
