package codes.jakob.semanticcoupling.utility

import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors


object Utilities {
    fun getResourceAsText(path: String): String {
        var fullPath: String = path
        if (!path.startsWith('/')) fullPath = "/$fullPath"
        return object {}.javaClass.getResource(fullPath).readText()
    }

    fun isNonEmptyWordEntry(word: String): Boolean {
        return (word != " " && word != "")
    }

    fun createCoroutineDispatcher() = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()).asCoroutineDispatcher()
}
