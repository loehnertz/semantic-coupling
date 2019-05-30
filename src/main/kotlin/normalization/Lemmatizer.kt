package codes.jakob.semanticcoupling.normalization

import codes.jakob.semanticcoupling.model.NaturalLanguage
import edu.stanford.nlp.simple.Sentence


class Lemmatizer(private val naturalLanguage: NaturalLanguage) : Normalizer {
    override fun normalizeWord(word: String): String {
        return Sentence(word).lemmas().first()
    }
}
