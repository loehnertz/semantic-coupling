package codes.jakob.semanticcoupling.model


data class Corpus(
    val documents: MutableSet<Document> = mutableSetOf()
)
