package com.github.loehnertz.semanticcoupling.tfidf

import com.github.loehnertz.semanticcoupling.model.Corpus
import com.github.loehnertz.semanticcoupling.model.Document
import com.github.loehnertz.semanticcoupling.model.Term
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking


class TfIdfCalculator(private val corpus: Corpus, documentSimilaritiesToCalculate: List<Pair<String, String>>?) {
    private val documentsToConsider: Set<String>? = documentSimilaritiesToCalculate?.flatMap { listOf(it.first, it.second) }?.toSet()
    private val idfCalculator = InverseDocumentFrequencyCalculator(corpus)

    fun calculateForAllTerms(): Corpus {
        val deferredDocuments: ArrayList<Deferred<Document>> = arrayListOf()
        for (document: Document in corpus.documents) {
            if (documentsToConsider != null && !documentsToConsider.contains(document.name)) continue
            deferredDocuments.add(GlobalScope.async { calculateForDocument(document) })
        }

        return runBlocking {
            return@runBlocking Corpus(deferredDocuments.map { it.await() }.toMutableSet())
        }
    }

    private fun calculateForDocument(document: Document): Document {
        val tfCalculator = TermFrequencyCalculator(document)

        for (term: Term in document.terms) {
            val tf: Double = tfCalculator.calculate(term)
            val idf: Double = idfCalculator.calculate(term)
            term.tfidf = (tf * idf)
        }

        return document
    }
}
