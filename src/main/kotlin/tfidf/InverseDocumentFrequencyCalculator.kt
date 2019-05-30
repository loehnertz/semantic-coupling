package codes.jakob.semanticcoupling.tfidf

import codes.jakob.semanticcoupling.model.Corpus
import codes.jakob.semanticcoupling.model.Document
import codes.jakob.semanticcoupling.model.Term


class InverseDocumentFrequencyCalculator(private val corpus: Corpus) {
    fun calculate(termToCheck: Term): Double {
        var numberOfDocumentsWithTermToCheck = 0

        for (document: Document in corpus.documents) {
            if (document.terms.contains(termToCheck)) numberOfDocumentsWithTermToCheck++
        }

        return Math.log((corpus.documents.size.toDouble() / numberOfDocumentsWithTermToCheck))
    }
}
