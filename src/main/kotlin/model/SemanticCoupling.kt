package codes.jakob.semanticcoupling.model

import codes.jakob.semanticcoupling.utility.DocumentPair


data class SemanticCoupling(
    val documents: DocumentPair,
    var score: Double? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SemanticCoupling

        if (documents != other.documents) return false

        return true
    }

    override fun hashCode(): Int {
        return documents.hashCode()
    }
}
