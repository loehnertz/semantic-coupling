package codes.jakob.semanticcoupling.similarity

import codes.jakob.semanticcoupling.model.Corpus
import codes.jakob.semanticcoupling.model.Document
import codes.jakob.semanticcoupling.model.SemanticCoupling


class SimilarityCalculator(private val corpus: Corpus, private val documentSimilaritiesToCalculate: List<Pair<String, String>>?) {
    fun calculateDocumentSimilarities(): List<SemanticCoupling> {
        val documentSimilarities: ArrayList<SemanticCoupling> = arrayListOf()

        for (document1: Document in corpus.documents) {
            for (document2: Document in corpus.documents) {
                if (document1 == document2) continue
                if (!documentSimilarityShouldBeCalculated(document1, document2)) continue
                if (documentSimilarities.firstOrNull { documentTripleSimilarityAlreadyCalculated(it, document1, document2) } != null) continue

                val allWords: List<String> = (document1.terms + document2.terms).map { it.word }.distinct().sorted()

                val document1WordVector: ArrayList<Double> = arrayListOf()
                val document2WordVector: ArrayList<Double> = arrayListOf()

                for (word: String in allWords) {
                    document1WordVector.add(document1.terms.find { it.word == word }?.tfidf ?: 0.0)
                    document2WordVector.add(document2.terms.find { it.word == word }?.tfidf ?: 0.0)
                }

                val cosineSimilarity: Double = calculateCosineSimilarity(document1WordVector.toTypedArray(), document2WordVector.toTypedArray())
                documentSimilarities.add(SemanticCoupling(documents = Pair(document1, document2), score = cosineSimilarity))
            }
        }

        return documentSimilarities.toList().sortedByDescending { it.score }
    }

    private fun calculateCosineSimilarity(vector1: Array<Double>, vector2: Array<Double>): Double {
        var dotProduct = 0.0
        var normA = 0.0
        var normB = 0.0

        for (i: Int in vector1.indices) {
            dotProduct += vector1[i] * vector2[i]
            normA += Math.pow(vector1[i], 2.0)
            normB += Math.pow(vector2[i], 2.0)
        }

        val cosineSimilarity: Double = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB))

        if (cosineSimilarity == Double.NaN) return 0.0
        return cosineSimilarity
    }

    private fun documentSimilarityShouldBeCalculated(document1: Document, document2: Document): Boolean {
        if (documentSimilaritiesToCalculate == null) return true
        return documentSimilaritiesToCalculate.contains(Pair(document1.name, document2.name))
    }

    private fun documentTripleSimilarityAlreadyCalculated(semanticCoupling: SemanticCoupling, document1: Document, document2: Document): Boolean {
        return ((semanticCoupling.documents.first == document1 && semanticCoupling.documents.second == document2) || (semanticCoupling.documents.first == document2 && semanticCoupling.documents.second == document1))
    }
}
