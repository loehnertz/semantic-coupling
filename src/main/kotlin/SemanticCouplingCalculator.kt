package codes.jakob.semanticcoupling

import codes.jakob.semanticcoupling.model.Corpus
import codes.jakob.semanticcoupling.model.Document
import codes.jakob.semanticcoupling.model.NaturalLanguage
import codes.jakob.semanticcoupling.model.NaturalLanguage.Companion.getNaturalLanguageByName
import codes.jakob.semanticcoupling.model.ProgrammingLanguage
import codes.jakob.semanticcoupling.model.ProgrammingLanguage.Companion.getProgrammingLanguageByName
import codes.jakob.semanticcoupling.parsing.programminglanguages.JavaSourceCodeParser
import codes.jakob.semanticcoupling.stemming.StemRetriever
import java.io.File


class SemanticCouplingCalculator(private val files: List<Map<String, String>>, private val programmingLanguage: ProgrammingLanguage, private val naturalLanguage: NaturalLanguage = DefaultNaturalLanguage) {
    constructor(files: List<File>, selectedProgrammingLanguage: String, selectedNaturalLanguage: String) : this(files.map { mapOf(it.name to it.readText()) }, getProgrammingLanguageByName(selectedProgrammingLanguage), getNaturalLanguageByName(selectedNaturalLanguage))

    fun calculate() {
        var corpus = Corpus()
        for (file: Map<String, String> in files) {
            for ((fileName: String, fileContents: String) in file) {
                val document: Document = parseFile(fileName, fileContents)
                corpus.documents.add(document)
            }
        }

        corpus = StemRetriever(naturalLanguage, corpus).stemDocuments()

        println(corpus)
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
