package codes.jakob.semanticcoupling.similarity

import codes.jakob.semanticcoupling.model.Corpus
import codes.jakob.semanticcoupling.model.Document


class SimilarityCalculator(private val corpus: Corpus) {
    fun calculateDocumentSimilarities(): List<Triple<Document, Document, Double>> {
        val documentSimilarities: ArrayList<Triple<Document, Document, Double>> = arrayListOf()

        for (document1: Document in corpus.documents) {
            for (document2: Document in corpus.documents) {
                if (document1 == document2) continue
                if (documentSimilarities.firstOrNull { documentTripleSimilarityAlreadyCalculated(it, document1, document2) } != null) continue

                val allWords: List<String> = (document1.terms + document2.terms).map { it.word }.distinct().sorted()

                val document1WordVector: ArrayList<Double> = arrayListOf()
                val document2WordVector: ArrayList<Double> = arrayListOf()

                for (word: String in allWords) {
                    document1WordVector.add(document1.terms.find { it.word == word }?.tfidf ?: 0.0)
                    document2WordVector.add(document2.terms.find { it.word == word }?.tfidf ?: 0.0)
                }

                val cosineSimilarity: Double = calculateCosineSimilarity(document1WordVector.toTypedArray(), document2WordVector.toTypedArray())
                documentSimilarities.add(Triple(document1, document2, cosineSimilarity))
            }
        }

        return documentSimilarities.toList().sortedByDescending { it.third }
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

    private fun documentTripleSimilarityAlreadyCalculated(similarityTriple: Triple<Document, Document, Double>, document1: Document, document2: Document): Boolean {
        return ((similarityTriple.first == document1 && similarityTriple.second == document2) || (similarityTriple.first == document2 && similarityTriple.second == document1))
    }
}
