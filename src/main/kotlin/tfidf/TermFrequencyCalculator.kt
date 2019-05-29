package codes.jakob.semanticcoupling.tfidf

import codes.jakob.semanticcoupling.model.Document
import codes.jakob.semanticcoupling.model.Term


class TermFrequencyCalculator(private val document: Document) {
    fun calculate(termToCheck: Term): Double {
        var frequency = 0.0

        for (term: Term in document.terms) {
            if (termToCheck == term) frequency++
        }

        return (frequency / document.terms.size)
    }
}
