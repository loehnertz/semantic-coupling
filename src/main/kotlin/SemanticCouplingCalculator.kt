package codes.jakob.semanticcoupling

import codes.jakob.semanticcoupling.model.Corpus
import codes.jakob.semanticcoupling.model.Document
import codes.jakob.semanticcoupling.model.NaturalLanguage
import codes.jakob.semanticcoupling.model.ProgrammingLanguage
import codes.jakob.semanticcoupling.model.ProgrammingLanguage.Companion.getProgrammingLanguageByName
import codes.jakob.semanticcoupling.parsing.languages.JavaSourceCodeParser
import java.io.File


class SemanticCouplingCalculator(selectedProgrammingLanguage: String, private val files: List<Map<String, String>>) {
    private val naturalLanguage: NaturalLanguage = DefaultNaturalLanguage
    private val programmingLanguage: ProgrammingLanguage = getProgrammingLanguageByName(selectedProgrammingLanguage)

    fun calculate() {
        val documents: ArrayList<Document> = arrayListOf()
        for (file: Map<String, String> in files) {
            for ((fileName: String, fileContents: String) in file) {
                documents.add(parseFile(fileName, fileContents))
            }
        }

        val corpus = Corpus(documents = documents)
    }

    private fun parseFile(fileName: String, fileContents: String): Document {
        return when (programmingLanguage) {
            ProgrammingLanguage.JAVA -> JavaSourceCodeParser(naturalLanguage, fileName, fileContents).parse()
        }
    }

    companion object {
        private val DefaultNaturalLanguage = NaturalLanguage.EN

        fun initWithFiles(language: String, files: List<File>): SemanticCouplingCalculator {
            return SemanticCouplingCalculator(language, files.map { mapOf(it.name to it.readText()) })
        }
    }
}
