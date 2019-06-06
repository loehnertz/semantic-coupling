package codes.jakob.semanticcoupling

import codes.jakob.semanticcoupling.lsi.LatentSemanticIndexer
import codes.jakob.semanticcoupling.model.*
import codes.jakob.semanticcoupling.model.NaturalLanguage.Companion.getNaturalLanguageByName
import codes.jakob.semanticcoupling.model.ProgrammingLanguage.Companion.getProgrammingLanguageByName
import codes.jakob.semanticcoupling.parsing.programminglanguages.JavaSourceCodeParser
import codes.jakob.semanticcoupling.similarity.SimilarityCalculator
import codes.jakob.semanticcoupling.tfidf.TfIdfCalculator
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking


@Suppress("unused")
class SemanticCouplingCalculator(private val files: List<Map<String, String>>, private val programmingLanguage: ProgrammingLanguage, private val naturalLanguage: NaturalLanguage = DefaultNaturalLanguage, private val fileSimilaritiesToCalculate: List<Pair<String, String>>? = null) {
    constructor(files: List<Map<String, String>>, selectedProgrammingLanguage: String, selectedNaturalLanguage: String, fileSimilaritiesToCalculate: List<List<String>>? = null) : this(files, getProgrammingLanguageByName(selectedProgrammingLanguage), getNaturalLanguageByName(selectedNaturalLanguage), fileSimilaritiesToCalculate?.map { Pair(it.first(), it.last()) })

    private var useLemmatization = true
    private var useLsi = true
    private var numberOfLsiDimensions: Int = DefaultNumberOfLsiDimensions
    private var maxLsiEpochs: Int = DefaultMaxLsiEpochs
    private lateinit var corpus: Corpus
    private var documentSimilarities: ArrayList<SemanticCoupling> = arrayListOf()

    fun calculate() {
        documentSimilarities.clear()

        val deferredDocuments: ArrayList<Deferred<Document>> = arrayListOf()
        for (file: Map<String, String> in files) {
            for ((fileName: String, fileContents: String) in file) {
                deferredDocuments.add(GlobalScope.async { parseFile(fileName, fileContents) })
            }
        }

        runBlocking { corpus = Corpus(deferredDocuments.map { it.await() }.toMutableSet()) }

        corpus = TfIdfCalculator(corpus, if (useLsi) null else fileSimilaritiesToCalculate).calculateForAllTerms()

        val similarities: List<SemanticCoupling> = SimilarityCalculator(corpus, fileSimilaritiesToCalculate, useLsi, numberOfLsiDimensions, maxLsiEpochs).calculateDocumentSimilarities()
        similarities.forEach { documentSimilarities.add(it) }
    }

    fun retrieveSimilaritiesAsListOfTriples(): List<Triple<String, String, Double>> {
        return documentSimilarities.map { Triple(it.documents.first.name, it.documents.second.name, it.score) }
    }

    fun retrieveSimilaritiesAsListsOfLists(): List<List<String>> {
        return documentSimilarities.map { listOf(it.documents.first.name, it.documents.second.name, it.score.toString()) }
    }

    fun useStemming() {
        useLemmatization = false
    }

    fun useLemmatization() {
        useLemmatization = true
    }

    fun doNotUseLsi() {
        useLsi = false
    }

    fun useLsi(dimensions: Int = numberOfLsiDimensions, maxEpochs: Int = maxLsiEpochs) {
        useLsi = true
        numberOfLsiDimensions = dimensions
        maxLsiEpochs = maxEpochs
    }

    private fun parseFile(fileName: String, fileContents: String): Document {
        return when (programmingLanguage) {
            ProgrammingLanguage.JAVA -> JavaSourceCodeParser(naturalLanguage, fileName, fileContents, useLemmatization).parse()
        }
    }

    companion object Constants {
        private val DefaultNaturalLanguage = NaturalLanguage.EN
        private const val DefaultNumberOfLsiDimensions: Int = LatentSemanticIndexer.NumberOfDimensions
        private const val DefaultMaxLsiEpochs: Int = LatentSemanticIndexer.MaxEpochs
    }
}
