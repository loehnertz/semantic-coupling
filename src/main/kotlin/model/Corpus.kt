package com.github.loehnertz.semanticcoupling.model


data class Corpus(
    val documents: MutableSet<Document> = mutableSetOf()
)
