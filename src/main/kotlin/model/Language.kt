package codes.jakob.semanticcoupling.model


enum class Language {
    JAVA;

    companion object {
        fun getLanguageByName(name: String): Language = valueOf(name.toUpperCase())
    }
}
