package com.github.loehnertz.semanticcoupling.normalization

import com.github.loehnertz.semanticcoupling.model.NaturalLanguage
import com.github.loehnertz.semanticcoupling.normalization.Stemmer.Constants.EndingsBasePath
import com.github.loehnertz.semanticcoupling.normalization.Stemmer.Constants.NaturalLanguagesStopWordPath
import com.github.loehnertz.semanticcoupling.utility.Utilities.getResourceAsText


class Stemmer(private val naturalLanguage: NaturalLanguage) : Normalizer {
    private val concatinatedRegex: Regex = buildConcatinatedRegex()

    override fun normalizeWord(word: String): String {
        return word.replace(concatinatedRegex, "")
    }

    private fun buildConcatinatedRegex(): Regex {
        return retrieveAllEndings()
            .joinToString("") { "(($it)\$)|" }
            .removeSuffix("|")
            .toRegex()
    }

    private fun retrieveAllEndings(): List<String> {
        val endingsFile: String = getResourceAsText("/$EndingsBasePath/$NaturalLanguagesStopWordPath/${naturalLanguage.name.toLowerCase()}.txt")
        return endingsFile.split("\n").filter { it != "" }
    }

    object Constants {
        const val EndingsBasePath = "endings"
        const val NaturalLanguagesStopWordPath = "naturallanguages"
    }
}
