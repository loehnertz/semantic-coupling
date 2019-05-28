package codes.jakob.semanticcoupling.model


data class SemanticCoupling(
    val documents: Pair<Document, Document>,
    val score: Double
)
