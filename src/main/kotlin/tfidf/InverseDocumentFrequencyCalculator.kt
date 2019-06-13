package com.github.loehnertz.semanticcoupling.tfidf

import com.github.loehnertz.semanticcoupling.model.Corpus
import com.github.loehnertz.semanticcoupling.model.Document
import com.github.loehnertz.semanticcoupling.model.Term


class InverseDocumentFrequencyCalculator(private val corpus: Corpus) {
    fun calculate(termToCheck: Term): Double {
        var numberOfDocumentsWithTermToCheck = 0

        for (document: Document in corpus.documents) {
            if (document.terms.contains(termToCheck)) numberOfDocumentsWithTermToCheck++
        }

        return Math.log((corpus.documents.size.toDouble() / numberOfDocumentsWithTermToCheck))
    }
}
