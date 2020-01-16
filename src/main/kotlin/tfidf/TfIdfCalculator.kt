package codes.jakob.semanticcoupling.tfidf

import codes.jakob.semanticcoupling.model.Corpus
import codes.jakob.semanticcoupling.model.Document
import codes.jakob.semanticcoupling.utility.mapConcurrently
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


class TfIdfCalculator(private val corpus: Corpus) {
    private val idfCalculator = InverseDocumentFrequencyCalculator(corpus)

    fun calculateForAllTerms(dispatcher: CoroutineDispatcher = Dispatchers.Default) = runBlocking {
        corpus.documents.mapConcurrently(dispatcher) { calculateForDocument(it) }
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
