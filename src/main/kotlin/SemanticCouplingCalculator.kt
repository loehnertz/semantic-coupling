package codes.jakob.semanticcoupling

import codes.jakob.semanticcoupling.lsi.LatentSemanticIndexer
import codes.jakob.semanticcoupling.model.*
import codes.jakob.semanticcoupling.model.NaturalLanguage.Companion.getNaturalLanguageByName
import codes.jakob.semanticcoupling.model.ProgrammingLanguage.Companion.getProgrammingLanguageByName
import codes.jakob.semanticcoupling.parsing.programminglanguages.JavaSourceCodeParser
import codes.jakob.semanticcoupling.similarity.SimilarityCalculator
import codes.jakob.semanticcoupling.tfidf.TfIdfCalculator
import codes.jakob.semanticcoupling.utility.mapConcurrently
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Suppress("unused")
class SemanticCouplingCalculator(private val files: Map<String, String>, private val programmingLanguage: ProgrammingLanguage, private val naturalLanguage: NaturalLanguage = DefaultNaturalLanguage, private val fileSimilaritiesToCalculate: List<Pair<String, String>>? = null) {
    constructor(files: Map<String, String>, selectedProgrammingLanguage: String, selectedNaturalLanguage: String, fileSimilaritiesToCalculate: List<List<String>>? = null) : this(files, getProgrammingLanguageByName(selectedProgrammingLanguage), getNaturalLanguageByName(selectedNaturalLanguage), fileSimilaritiesToCalculate?.map { Pair(it.first(), it.last()) })

    private val logger: Logger = LoggerFactory.getLogger(SemanticCouplingCalculator::class.java)

    private var useLemmatization = true
    private var useLsi = true
    private var numberOfLsiDimensions: Int = DefaultNumberOfLsiDimensions
    private var maxLsiEpochs: Int = DefaultMaxLsiEpochs
    private val documentSimilarities: ArrayList<SemanticCoupling> = arrayListOf()
    private lateinit var corpus: Corpus

    fun calculate() = runBlocking {
        documentSimilarities.clear()
        corpus = Corpus(files.entries.mapConcurrently { parseFile(fileName = it.key, fileContents = it.value) }.toMutableSet()).also { logger.info("Finished parsing ${it.documents.size} files.") }
        corpus = TfIdfCalculator(corpus, if (useLsi) null else fileSimilaritiesToCalculate).calculateForAllTerms().also { logger.info("Finished processing ${it.documents.sumBy { document -> document.terms.size }} terms.") }
        SimilarityCalculator(corpus, fileSimilaritiesToCalculate, useLsi, numberOfLsiDimensions, maxLsiEpochs).calculateDocumentSimilarities().forEach { documentSimilarities.add(it) }.also { logger.info("Finished calculating all semantic similarity pairs.") }
    }

    fun retrieveSimilaritiesAsListOfTriples(): List<Triple<String, String, Double>> {
        return documentSimilarities.map { Triple(it.documents.first.name, it.documents.second.name, it.score) }
    }

    fun retrieveSimilaritiesAsListOfLists(): List<List<String>> {
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
