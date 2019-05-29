package codes.jakob.semanticcoupling.model


data class Document(
    val name: String,
    val terms: List<Term>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Document

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return 31 * name.hashCode()
    }
}
