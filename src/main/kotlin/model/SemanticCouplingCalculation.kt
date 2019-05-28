package codes.jakob.semanticcoupling.model


data class SemanticCouplingCalculation(
    val documentPairs: List<SemanticCoupling>
) {
    fun getSemanticCoupling(documentOne: Document, documentTwo: Document): SemanticCoupling? = documentPairs.firstOrNull { it.documents == Pair(documentOne, documentTwo) || it.documents == Pair(documentTwo, documentOne)}
}
