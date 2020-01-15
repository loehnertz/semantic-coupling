package codes.jakob.semanticcoupling.tfidf

import codes.jakob.semanticcoupling.model.Document
import codes.jakob.semanticcoupling.model.Term


class TermFrequencyCalculator(private val document: Document) {
    fun calculate(termToCheck: Term): Double {
        var frequency = 0
        for (termEntry in document.terms) {
            for (term in termEntry.value) {
                if (termToCheck == term) frequency++
            }
        }
        return (frequency.toDouble() / document.terms.size.toDouble())
    }
}
