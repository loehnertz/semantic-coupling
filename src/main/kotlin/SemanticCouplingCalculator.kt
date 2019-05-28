package codes.jakob.semanticcoupling

import codes.jakob.semanticcoupling.model.Language
import codes.jakob.semanticcoupling.model.Language.Companion.getLanguageByName
import java.io.File


class SemanticCouplingCalculator(selectedLanguage: String, private val files: List<Map<String, String>>) {
    private val language: Language = getLanguageByName(selectedLanguage)

    fun calculate() {

    }

    companion object {
        fun initWithFiles(language: String, files: List<File>): SemanticCouplingCalculator {
            return SemanticCouplingCalculator(language, files.map { mapOf(it.name to it.readText()) })
        }
    }
}
