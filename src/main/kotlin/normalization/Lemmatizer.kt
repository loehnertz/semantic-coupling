package codes.jakob.semanticcoupling.normalization

import edu.stanford.nlp.simple.Sentence


class Lemmatizer : Normalizer {
    override fun normalizeWord(word: String): String {
        return Sentence(word).lemmas().first()
    }
}
