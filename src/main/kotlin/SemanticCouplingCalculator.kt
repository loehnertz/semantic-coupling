package codes.jakob.semanticcoupling

import codes.jakob.semanticcoupling.lsi.LatentSemanticIndexer
import codes.jakob.semanticcoupling.model.*
import codes.jakob.semanticcoupling.parsing.programminglanguages.JavaSourceCodeParser
import codes.jakob.semanticcoupling.similarity.SimilarityCalculator
import codes.jakob.semanticcoupling.tfidf.TfIdfCalculator
import codes.jakob.semanticcoupling.utility.Utilities
import codes.jakob.semanticcoupling.utility.mapConcurrently
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Suppress("unused")
class SemanticCouplingCalculator(private val files: Map<String, String>, private val programmingLanguage: ProgrammingLanguage, private val naturalLanguage: NaturalLanguage = DefaultNaturalLanguage, private val fileSimilaritiesToCalculate: List<Set<String>>? = null) {
    private val logger: Logger = LoggerFactory.getLogger(SemanticCouplingCalculator::class.java)

    private var useLemmatization = true
    private var useLsi = false
    private var numberOfLsiDimensions: Int = DefaultNumberOfLsiDimensions
    private var maxLsiEpochs: Int = DefaultMaxLsiEpochs

    private val documentsToConsider: Set<String>? = fileSimilaritiesToCalculate?.flatten()?.toSet()
    private lateinit var corpus: Corpus
    private lateinit var dispatcher: ExecutorCoroutineDispatcher

    fun calculate(): List<SemanticCoupling> {
        dispatcher = Utilities.createCoroutineDispatcher()
        constructCorpus(dispatcher).also { logger.info("Finished parsing ${corpus.documents.size} files.") }
        TfIdfCalculator(corpus).calculateForAllTerms(dispatcher).also { logger.info("Finished processing ${corpus.documents.sumBy { document -> document.terms.size }} term groups.") }
        return SimilarityCalculator(corpus, fileSimilaritiesToCalculate, useLsi, numberOfLsiDimensions, maxLsiEpochs).calculateDocumentSimilarities(dispatcher).also { dispatcher.close() }.also { logger.info("Finished calculating ${it.size} semantic similarity pairs.") }
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

    private fun constructCorpus(dispatcher: CoroutineDispatcher) = runBlocking {
        val documents: MutableSet<Document> = files.entries
            .mapConcurrently(dispatcher) { parseFile(fileName = it.key, fileContents = it.value) }
            .filter { documentsToConsider?.contains(it.name) ?: true }
            .toMutableSet()
        corpus = Corpus(documents)
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
