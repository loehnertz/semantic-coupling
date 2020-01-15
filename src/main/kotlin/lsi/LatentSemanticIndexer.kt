package codes.jakob.semanticcoupling.lsi

import codes.jakob.semanticcoupling.model.Corpus
import codes.jakob.semanticcoupling.model.Document
import codes.jakob.semanticcoupling.utility.Word
import com.aliasi.matrix.SvdMatrix


class LatentSemanticIndexer(private val corpus: Corpus, private val dimensions: Int = NumberOfDimensions, private val maxEpochs: Int = MaxEpochs) {
    private val documentMap: Map<Document, Int> = constructDocumentMap()
    private val termDocumentMatrix: Array<DoubleArray> = constructTermDocumentMatrix()
    private val documentVectors: Array<out DoubleArray> = constructDocumentVectors()

    fun retrieveDocumentVectors(documentA: Document, documentB: Document): Pair<DoubleArray, DoubleArray> {
        val indexA: Int = documentMap[documentA]
                          ?: throw IllegalAccessException("Document does not exist in a local corpus.")
        val indexB: Int = documentMap[documentB]
                          ?: throw IllegalAccessException("Document does not exist in a local corpus.")

        return Pair(documentVectors[indexA], documentVectors[indexB])
    }

    private fun constructDocumentVectors(): Array<out DoubleArray> {
        val matrix: SvdMatrix = SvdMatrix.svd(
            termDocumentMatrix,
            dimensions,
            FeatureInit,
            InitialLearningRate,
            AnnealingRate,
            Regularization,
            null,
            MinImprovement,
            MinEpochs,
            maxEpochs
        )

        return matrix.rightSingularVectors()
    }

    private fun constructDocumentMap(): Map<Document, Int> {
        return corpus.documents.mapIndexed { index, document -> Pair(document, index) }.toMap()
    }

    private fun constructTermDocumentMatrix(): Array<DoubleArray> {
        val allWords: Set<Word> = corpus.documents.flatMap { it.terms.keys }.toSet()
        return allWords.map { constructTermRow(it) }.toTypedArray()
    }

    private fun constructTermRow(wordForRow: Word): DoubleArray {
        return corpus.documents.map { document: Document ->
            document.terms[wordForRow]?.first()?.tfidf ?: 0.0
        }.toDoubleArray()
    }

    companion object Constants {
        const val MaxEpochs = 1000
        const val NumberOfDimensions = 100
        private const val FeatureInit = 0.01
        private const val InitialLearningRate = 0.005
        private const val AnnealingRate = 1000.0
        private const val Regularization = 0.00
        private const val MinImprovement = 0.0000
        private const val MinEpochs = 10
    }
}
