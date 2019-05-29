package codes.jakob.semanticcoupling.tfidf

import codes.jakob.semanticcoupling.model.Corpus
import codes.jakob.semanticcoupling.model.Document
import codes.jakob.semanticcoupling.model.Term


class TfIdfCalculator(private val corpus: Corpus) {
    fun calculateForAllTerms(): Corpus {
        val idfCalculator = InverseDocumentFrequencyCalculator(corpus)

        for (document: Document in corpus.documents) {
            val tfCalculator = TermFrequencyCalculator(document)

            for (term: Term in document.terms) {
                val tf: Double = tfCalculator.calculate(term)
                val idf: Double = idfCalculator.calculate(term)
                term.tfidf = (tf * idf)
            }
        }

        val documents: ArrayList<Document> = arrayListOf()
        for (document: Document in corpus.documents) {
            documents.add(
                Document(
                    name = document.name,
                    terms = document.terms.distinct().sortedByDescending { it.tfidf!! }
                )
            )
        }

        return Corpus(documents)
    }
}
