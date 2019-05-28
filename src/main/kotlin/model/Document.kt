package codes.jakob.semanticcoupling.model


data class Document(
    val name: String,
    val content: ArrayList<String> = arrayListOf()
)
