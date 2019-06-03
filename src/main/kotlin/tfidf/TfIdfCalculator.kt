package codes.jakob.semanticcoupling.tfidf

import codes.jakob.semanticcoupling.model.Corpus
import codes.jakob.semanticcoupling.model.Document
import codes.jakob.semanticcoupling.model.Term


class TfIdfCalculator(private val corpus: Corpus, documentSimilaritiesToCalculate: List<Pair<String, String>>?) {
    private val documentsToConsider: Set<String>? = documentSimilaritiesToCalculate?.flatMap { listOf(it.first, it.second) }?.toSet()

    fun calculateForAllTerms(): Corpus {
        val idfCalculator = InverseDocumentFrequencyCalculator(corpus)

        for (document: Document in corpus.documents) {
            if (documentsToConsider != null && !documentsToConsider.contains(document.name)) continue

            val tfCalculator = TermFrequencyCalculator(document)

            for (term: Term in document.terms) {
                val tf: Double = tfCalculator.calculate(term)
                val idf: Double = idfCalculator.calculate(term)
                term.tfidf = (tf * idf)
            }
        }

        return corpus
    }
}
