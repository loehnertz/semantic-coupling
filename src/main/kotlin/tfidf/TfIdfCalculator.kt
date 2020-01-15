package codes.jakob.semanticcoupling.tfidf

import codes.jakob.semanticcoupling.model.Corpus
import codes.jakob.semanticcoupling.model.Document
import codes.jakob.semanticcoupling.utility.mapConcurrently
import kotlinx.coroutines.runBlocking


class TfIdfCalculator(private val corpus: Corpus, documentSimilaritiesToCalculate: List<Pair<String, String>>?) {
    private val documentsToConsider: Set<String>? = documentSimilaritiesToCalculate?.flatMap { listOf(it.first, it.second) }?.toSet()
    private val idfCalculator = InverseDocumentFrequencyCalculator(corpus)

    fun calculateForAllTerms(): Corpus = runBlocking {
        return@runBlocking Corpus(corpus.documents.filter {
            documentsToConsider?.contains(it.name) ?: true
        }.mapConcurrently { calculateForDocument(it) }.toMutableSet())
    }

    private fun calculateForDocument(document: Document): Document {
        val tfCalculator = TermFrequencyCalculator(document)

        for (termEntry in document.terms) {
            for (term in termEntry.value) {
                val tf: Double = tfCalculator.calculate(term)
                val idf: Double = idfCalculator.calculate(term)
                term.tfidf = (tf * idf)
            }
        }

        return document
    }
}
