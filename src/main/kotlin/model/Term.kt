package com.github.loehnertz.semanticcoupling.model


data class Term(
    val word: String,
    var tfidf: Double? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Term

        if (word != other.word) return false

        return true
    }

    override fun hashCode(): Int {
        return 31 * word.hashCode()
    }
}
