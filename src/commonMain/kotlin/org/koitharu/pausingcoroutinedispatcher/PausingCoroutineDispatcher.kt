package org.koitharu.pausingcoroutinedispatcher

import kotlinx.coroutines.*
import org.koitharu.pausingcoroutinedispatcher.internal.PausingDispatchQueue
import org.koitharu.pausingcoroutinedispatcher.internal.PausingDispatcherImpl
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun CoroutineScope.launchPausing(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): PausingJob {
    val dispatcher = PausingDispatcher(this, context)
    val job = launch(context + dispatcher.queue + dispatcher, start, block)
    return PausingJob(job, dispatcher.queue)
}

fun <T> CoroutineScope.asyncPausing(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): PausingDeferred<T> {
    val dispatcher = PausingDispatcher(this, context)
    val deferred = async(context + dispatcher.queue + dispatcher, start, block)
    return PausingDeferred(deferred, dispatcher.queue)
}

private fun PausingDispatcher(scope: CoroutineScope, newContext: CoroutineContext): PausingDispatcherImpl {
    val baseDispatcher = newContext[CoroutineDispatcher]
        ?: scope.coroutineContext[CoroutineDispatcher]
        ?: Dispatchers.Default
    val queue = newContext[PausingDispatchQueue]
        ?: scope.coroutineContext[PausingDispatchQueue]
        ?: PausingDispatchQueue()
    return PausingDispatcherImpl(queue, baseDispatcher)
}