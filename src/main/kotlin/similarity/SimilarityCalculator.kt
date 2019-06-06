package codes.jakob.semanticcoupling.similarity

import codes.jakob.semanticcoupling.lsi.LatentSemanticIndexer
import codes.jakob.semanticcoupling.model.Corpus
import codes.jakob.semanticcoupling.model.Document
import codes.jakob.semanticcoupling.model.SemanticCoupling


class SimilarityCalculator(private val corpus: Corpus, private val documentSimilaritiesToCalculate: List<Pair<String, String>>?, private val useLsi: Boolean, numberOfLsiDimensions: Int, maxLsiEpochs: Int) {
    private lateinit var latentSemanticIndexer: LatentSemanticIndexer

    init {
        if (useLsi) latentSemanticIndexer = LatentSemanticIndexer(corpus = corpus, dimensions = numberOfLsiDimensions, maxEpochs = maxLsiEpochs)
    }

    fun calculateDocumentSimilarities(): List<SemanticCoupling> {
        val documentSimilarities: ArrayList<SemanticCoupling> = arrayListOf()

        for (documentA: Document in corpus.documents) {
            for (documentB: Document in corpus.documents) {
                if (documentA == documentB) continue
                if (!documentSimilarityShouldBeCalculated(documentA, documentB)) continue
                if (documentSimilarities.firstOrNull { documentTripleSimilarityAlreadyCalculated(it, documentA, documentB) } != null) continue

                val documentSimilarity: Double = calculateDocumentSimilarity(documentA, documentB)
                val semanticCoupling = SemanticCoupling(documents = Pair(documentA, documentB), score = documentSimilarity)

                documentSimilarities.add(semanticCoupling)
            }
        }

        return documentSimilarities.toList().sortedByDescending { it.score }
    }

    private fun calculateDocumentSimilarity(documentA: Document, documentB: Document): Double {
        val documentVectors: Pair<DoubleArray, DoubleArray> = retrieveDocumentVectors(documentA, documentB)
        return calculateCosineSimilarity(documentVectors.first, documentVectors.second)
    }

    private fun retrieveDocumentVectors(documentA: Document, documentB: Document): Pair<DoubleArray, DoubleArray> {
        return if (useLsi) {
            latentSemanticIndexer.retrieveDocumentVectors(documentA, documentB)
        } else {
            constructDocumentVectors(documentA, documentB)
        }
    }

    private fun calculateCosineSimilarity(vectorA: DoubleArray, vectorB: DoubleArray): Double {
        var dotProduct = 0.0
        var normA = 0.0
        var normB = 0.0

        for (i: Int in vectorA.indices) {
            dotProduct += vectorA[i] * vectorB[i]
            normA += Math.pow(vectorA[i], 2.0)
            normB += Math.pow(vectorB[i], 2.0)
        }

        val cosineSimilarity: Double = (dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)))

        if (cosineSimilarity == Double.NaN) return 0.0
        return cosineSimilarity
    }

    private fun constructDocumentVectors(documentA: Document, documentB: Document): Pair<DoubleArray, DoubleArray> {
        val allWords: List<String> = (documentA.terms + documentB.terms).map { it.word }.distinct().sorted()

        val documentAWordVector: ArrayList<Double> = arrayListOf()
        val documentBWordVector: ArrayList<Double> = arrayListOf()

        for (word: String in allWords) {
            documentAWordVector.add(documentA.terms.find { it.word == word }?.tfidf ?: 0.0)
            documentBWordVector.add(documentB.terms.find { it.word == word }?.tfidf ?: 0.0)
        }

        return Pair(documentAWordVector.toDoubleArray(), documentBWordVector.toDoubleArray())
    }

    private fun documentSimilarityShouldBeCalculated(document1: Document, document2: Document): Boolean {
        if (documentSimilaritiesToCalculate == null) return true
        return documentSimilaritiesToCalculate.contains(Pair(document1.name, document2.name))
    }

    private fun documentTripleSimilarityAlreadyCalculated(semanticCoupling: SemanticCoupling, document1: Document, document2: Document): Boolean {
        return ((semanticCoupling.documents.first == document1 && semanticCoupling.documents.second == document2) || (semanticCoupling.documents.first == document2 && semanticCoupling.documents.second == document1))
    }
}
