package codes.jakob.semanticcoupling.parsing

import codes.jakob.semanticcoupling.model.Document
import codes.jakob.semanticcoupling.model.NaturalLanguage
import codes.jakob.semanticcoupling.model.ProgrammingLanguage
import codes.jakob.semanticcoupling.model.Term
import codes.jakob.semanticcoupling.normalization.Lemmatizer
import codes.jakob.semanticcoupling.normalization.Normalizer
import codes.jakob.semanticcoupling.normalization.Stemmer
import codes.jakob.semanticcoupling.utility.Utilities.getResourceAsText
import codes.jakob.semanticcoupling.utility.Utilities.isNonEmptyWordEntry


abstract class AbstractSourceCodeParser(private val selectedNaturalLanguage: NaturalLanguage, private val fileName: String, private val fileContents: String, private val useLemmatizer: Boolean) {
    abstract val packageRegex: Regex

    abstract val importRegex: Regex

    abstract val commentRegex: Regex

    abstract val programmingLanguageStopWords: List<String>

    abstract fun tokenize(line: String): List<String>


    private val naturalLanguageStopWords: List<String> = retrieveNaturalLanguageStopWords(selectedNaturalLanguage)

    fun parse(): Document {
        val normalizer: Normalizer = retrieveNormalizer()

        val tokenizedFileContents: List<Term> =
            fileContents
                .split("\n")
                .filter { !isPackage(it) }
                .filter { !isImport(it) }
                .filter { !isComment(it) }
                .flatMap { tokenize(it) }
                .asSequence()
                .filter { !naturalLanguageStopWords.contains(it) }
                .filter { !it.matches(NumbersRegex) }
                .filter { !it.matches(SingleCharacterRegex) }
                .filter { isNonEmptyWordEntry(it) }
                .map { it.toLowerCase() }
                .map { normalizer.normalizeWord(it) }
                .map { Term(word = it) }
                .toList()

        return Document(name = fileName, terms = tokenizedFileContents)
    }

    fun retrieveProgrammingLanguageStopWords(programmingLanguage: ProgrammingLanguage): List<String> {
        val stopWordsFile: String = getResourceAsText("/$StopWordBasePath/$ProgrammingLanguagesStopWordPath/${programmingLanguage.name.toLowerCase()}.txt")
        return stopWordsFile.split("\n").filter { it != "" }
    }

    private fun retrieveNaturalLanguageStopWords(naturalLanguage: NaturalLanguage): List<String> {
        val stopWordsFile: String = getResourceAsText("/$StopWordBasePath/$NaturalLanguagesStopWordPath/${naturalLanguage.name.toLowerCase()}.txt")
        return stopWordsFile.split("\n").filter { it != "" }
    }

    private fun retrieveNormalizer(): Normalizer {
        return if (useLemmatizer) {
            Lemmatizer()
        } else {
            Stemmer(selectedNaturalLanguage)
        }
    }

    private fun isPackage(line: String): Boolean {
        return packageRegex.containsMatchIn(line)
    }

    private fun isImport(line: String): Boolean {
        return importRegex.containsMatchIn(line)
    }

    private fun isComment(line: String): Boolean {
        return commentRegex.containsMatchIn(line)
    }

    companion object Constants {
        val NumbersRegex: Regex = Regex("\\d+")
        val NonWordCharacterRegex: Regex = Regex("\\W+")
        val SingleCharacterRegex: Regex = Regex(".", RegexOption.DOT_MATCHES_ALL)
        private const val StopWordBasePath = "stopwords"
        private const val NaturalLanguagesStopWordPath = "naturallanguages"
        private const val ProgrammingLanguagesStopWordPath = "programminglanguages"
    }
}
