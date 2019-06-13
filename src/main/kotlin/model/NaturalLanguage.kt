package com.github.loehnertz.semanticcoupling.model


enum class NaturalLanguage {
    EN;

    companion object {
        fun getNaturalLanguageByName(name: String): NaturalLanguage = valueOf(name.toUpperCase())
    }
}
