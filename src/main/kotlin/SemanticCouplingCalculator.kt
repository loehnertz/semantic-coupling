package codes.jakob.semanticcoupling

import codes.jakob.semanticcoupling.model.Corpus
import codes.jakob.semanticcoupling.model.Document
import codes.jakob.semanticcoupling.model.NaturalLanguage
import codes.jakob.semanticcoupling.model.NaturalLanguage.Companion.getNaturalLanguageByName
import codes.jakob.semanticcoupling.model.ProgrammingLanguage
import codes.jakob.semanticcoupling.model.ProgrammingLanguage.Companion.getProgrammingLanguageByName
import codes.jakob.semanticcoupling.parsing.programminglanguages.JavaSourceCodeParser
import codes.jakob.semanticcoupling.similarity.SimilarityCalculator
import codes.jakob.semanticcoupling.tfidf.TfIdfCalculator
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.File


@Suppress("unused")
class SemanticCouplingCalculator(private val files: List<Map<String, String>>, private val programmingLanguage: ProgrammingLanguage, private val naturalLanguage: NaturalLanguage = DefaultNaturalLanguage) {
    constructor(files: List<Map<String, String>>, selectedProgrammingLanguage: String, selectedNaturalLanguage: String) : this(files, getProgrammingLanguageByName(selectedProgrammingLanguage), getNaturalLanguageByName(selectedNaturalLanguage))

    private var useLemmatization = true
    private val similarities: ArrayList<Triple<String, String, Double>> = arrayListOf()

    fun calculate() {
        val deferredDocuments: ArrayList<Deferred<Document>> = arrayListOf()
        for (file: Map<String, String> in files) {
            for ((fileName: String, fileContents: String) in file) {
                deferredDocuments.add(GlobalScope.async { parseFile(fileName, fileContents) })
            }
        }

        runBlocking {
            var corpus = Corpus(ArrayList(deferredDocuments.map { it.await() }))
            corpus = TfIdfCalculator(corpus).calculateForAllTerms()

            val documentSimilarities: List<Triple<Document, Document, Double>> = SimilarityCalculator(corpus).calculateDocumentSimilarities()
            documentSimilarities.forEach { similarities.add(Triple(it.first.name, it.second.name, it.third)) }
        }
    }

    fun retrieveSimilaritiesAsListOfTriples(): List<Triple<String, String, Double>> {
        return similarities.toList()
    }

    fun retrieveSimilaritiesAsListsOfLists(): List<List<String>> {
        return similarities.map { listOf(it.first, it.second, it.third.toString()) }
    }

    fun useStemming() {
        useLemmatization = false
    }

    fun useLemmatization() {
        useLemmatization = true
    }

    private fun parseFile(fileName: String, fileContents: String): Document {
        return when (programmingLanguage) {
            ProgrammingLanguage.JAVA -> JavaSourceCodeParser(naturalLanguage, fileName, fileContents, useLemmatization).parse()
        }
    }

    companion object Constants {
        private val DefaultNaturalLanguage = NaturalLanguage.EN
    }
}
