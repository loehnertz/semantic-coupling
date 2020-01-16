package codes.jakob.semanticcoupling.utility

import kotlinx.coroutines.*


suspend fun <A, B> Iterable<A>.mapConcurrently(dispatcher: CoroutineDispatcher = Dispatchers.Default, transform: suspend (A) -> B): List<B> {
    return coroutineScope {
        map { async(dispatcher) { transform(it) } }.awaitAll()
    }
}
