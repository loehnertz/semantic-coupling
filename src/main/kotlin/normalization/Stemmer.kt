package codes.jakob.semanticcoupling.normalization

import codes.jakob.semanticcoupling.model.Corpus
import codes.jakob.semanticcoupling.model.Document
import codes.jakob.semanticcoupling.model.NaturalLanguage
import codes.jakob.semanticcoupling.model.Term
import codes.jakob.semanticcoupling.stemming.StemRetriever.Constants.EndingsBasePath
import codes.jakob.semanticcoupling.stemming.StemRetriever.Constants.NaturalLanguagesStopWordPath
import codes.jakob.semanticcoupling.utility.Utilities.getResourceAsText
import codes.jakob.semanticcoupling.utility.Utilities.isNonEmptyWordEntry


class StemRetriever(private val naturalLanguage: NaturalLanguage, private val corpus: Corpus) {
    fun stemDocuments(): Corpus {
        val concatinatedRegex: Regex = buildConcatinatedRegex()

        val documents: ArrayList<Document> = ArrayList(corpus.documents.map { document ->
            Document(
                name = document.name,
                terms = document.terms
                    .map { it.word.replace(concatinatedRegex, "") }
                    .filter { isNonEmptyWordEntry(it) }
                    .map { Term(word = it) }
            )
        })

        return Corpus(documents)
    }

    private fun buildConcatinatedRegex(): Regex {
        return retrieveAllEndings()
            .joinToString("") { "(($it)\$)|" }
            .removeSuffix("|")
            .toRegex()
    }

    private fun retrieveAllEndings(): List<String> {
        val endingsFile: String = getResourceAsText("$EndingsBasePath/$NaturalLanguagesStopWordPath/${naturalLanguage.name.toLowerCase()}.txt")
        return endingsFile.split("\n").filter { it != "" }
    }

    object Constants {
        const val EndingsBasePath = "endings"
        const val NaturalLanguagesStopWordPath = "naturallanguages"
    }
}
