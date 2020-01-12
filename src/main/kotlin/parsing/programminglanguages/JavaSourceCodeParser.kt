package codes.jakob.semanticcoupling.parsing.programminglanguages

import codes.jakob.semanticcoupling.model.NaturalLanguage
import codes.jakob.semanticcoupling.model.ProgrammingLanguage
import codes.jakob.semanticcoupling.parsing.AbstractSourceCodeParser


class JavaSourceCodeParser(selectedNaturalLanguage: NaturalLanguage, fileName: String, fileContents: String, useLemmatization: Boolean) : AbstractSourceCodeParser(selectedNaturalLanguage, fileName, fileContents, useLemmatization) {
    override val programmingLanguageStopWords: List<String> = retrieveProgrammingLanguageStopWords(ParserProgrammingLanguage)
    override val packageRegex: Regex = PackageRegex
    override val importRegex: Regex = ImportRegex
    override val commentRegex: Regex = CommentRegex

    override fun tokenize(line: String): List<String> {
        val stringLiterals: List<String> =
            DoubleQuoteStringRegex
                .findAll(line)
                .asIterable()
                .map { it.value.replace("\\", "") }
                .flatMap { it.replace(NonWordCharacterRegex, " ").split(' ') }

        val tokens: List<String> =
            line
                .replace(DoubleQuoteStringRegex, "")
                .split(' ')
                .flatMap { it.replace(NonWordCharacterRegex, " ").split(' ') }
                .filter { !programmingLanguageStopWords.contains(it) }
                .flatMap { it.replace(CamelCaseRegex, CamelCaseToSpaceReplacement).split(' ') }

        return (tokens + stringLiterals).flatMap { it.split(Regex("\\t")) }
    }

    companion object Constants {
        private const val CamelCaseToSpaceReplacement: String = "\$1\$4 \$2\$3\$5"
        private val CamelCaseRegex: Regex = Regex("([A-Z])([A-Z])([a-z])|([a-z])([A-Z])")
        private val PackageRegex: Regex = Regex("^(package)", RegexOption.MULTILINE)
        private val ImportRegex: Regex = Regex("^(import)", RegexOption.MULTILINE)
        private val CommentRegex: Regex = Regex("(^\\s*/+)|(^\\s*\\*+)", RegexOption.MULTILINE)
        private val DoubleQuoteStringRegex: Regex = Regex("(?<=\")(.*?)(?=(?<!\\\\)\")", RegexOption.DOT_MATCHES_ALL)
        private val ParserProgrammingLanguage: ProgrammingLanguage = ProgrammingLanguage.JAVA
    }
}
