package codes.jakob.semanticcoupling.tfidf

import codes.jakob.semanticcoupling.model.Corpus
import codes.jakob.semanticcoupling.model.Term
import kotlin.math.ln


class InverseDocumentFrequencyCalculator(private val corpus: Corpus) {
    fun calculate(termToCheck: Term): Double {
        var numberOfDocumentsWithTermToCheck = 0
        for (document in corpus.documents) {
            if (document.terms.containsKey(termToCheck.word)) numberOfDocumentsWithTermToCheck++
        }
        return ln((corpus.documents.size.toDouble() / numberOfDocumentsWithTermToCheck.toDouble()))
    }
}
