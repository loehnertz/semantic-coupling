package codes.jakob.semanticcoupling

import codes.jakob.semanticcoupling.model.Corpus
import codes.jakob.semanticcoupling.model.Document
import codes.jakob.semanticcoupling.model.NaturalLanguage
import codes.jakob.semanticcoupling.model.NaturalLanguage.Companion.getNaturalLanguageByName
import codes.jakob.semanticcoupling.model.ProgrammingLanguage
import codes.jakob.semanticcoupling.model.ProgrammingLanguage.Companion.getProgrammingLanguageByName
import codes.jakob.semanticcoupling.parsing.programminglanguages.JavaSourceCodeParser
import codes.jakob.semanticcoupling.similarity.SimilarityCalculator
import codes.jakob.semanticcoupling.stemming.StemRetriever
import codes.jakob.semanticcoupling.tfidf.TfIdfCalculator


class SemanticCouplingCalculator(private val files: List<Map<String, String>>, private val programmingLanguage: ProgrammingLanguage, private val naturalLanguage: NaturalLanguage = DefaultNaturalLanguage) {
    constructor(files: List<Map<String, String>>, selectedProgrammingLanguage: String, selectedNaturalLanguage: String) : this(files, getProgrammingLanguageByName(selectedProgrammingLanguage), getNaturalLanguageByName(selectedNaturalLanguage))

    private val similarities: ArrayList<Triple<String, String, Double>> = arrayListOf()

    fun calculate() {
        var corpus = Corpus()
        for (file: Map<String, String> in files) {
            for ((fileName: String, fileContents: String) in file) {
                val document: Document = parseFile(fileName, fileContents)
                corpus.documents.add(document)
            }
        }

        corpus = StemRetriever(naturalLanguage, corpus).stemDocuments()
        corpus = TfIdfCalculator(corpus).calculateForAllTerms()

        val documentSimilarities: List<Triple<Document, Document, Double>> = SimilarityCalculator(corpus).calculateDocumentSimilarities()

        documentSimilarities.forEach { similarities.add(Triple(it.first.name, it.second.name, it.third)) }
    }

    fun retrieveSimilaritiesAsListOfTriples(): List<Triple<String, String, Double>> {
        return similarities.toList()
    }

    fun retrieveSimilaritiesAsListsOfLists(): List<List<String>> {
        return similarities.map { listOf(it.first, it.second, it.third.toString()) }
    }

    private fun parseFile(fileName: String, fileContents: String): Document {
        return when (programmingLanguage) {
            ProgrammingLanguage.JAVA -> JavaSourceCodeParser(naturalLanguage, fileName, fileContents).parse()
        }
    }

    companion object Constants {
        private val DefaultNaturalLanguage = NaturalLanguage.EN
    }
}
