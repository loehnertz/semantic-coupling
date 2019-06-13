package com.github.loehnertz.semanticcoupling.tfidf

import com.github.loehnertz.semanticcoupling.model.Document
import com.github.loehnertz.semanticcoupling.model.Term


class TermFrequencyCalculator(private val document: Document) {
    fun calculate(termToCheck: Term): Double {
        var frequency = 0

        for (term: Term in document.terms) {
            if (termToCheck == term) frequency++
        }

        return (frequency.toDouble() / document.terms.size)
    }
}
