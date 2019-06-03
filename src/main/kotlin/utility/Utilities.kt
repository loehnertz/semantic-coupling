package codes.jakob.semanticcoupling.utility


object Utilities {
    fun getResourceAsText(path: String): String {
        var fullPath: String = path
        if (!path.startsWith('/')) fullPath = "/$fullPath"
        return object {}.javaClass.getResource(fullPath).readText()
    }

    fun isNonEmptyWordEntry(word: String): Boolean {
        return (word != " " && word != "")
    }
}
