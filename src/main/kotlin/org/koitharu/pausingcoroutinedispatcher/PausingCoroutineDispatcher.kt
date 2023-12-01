package org.koitharu.pausingcoroutinedispatcher

import kotlinx.coroutines.*
import org.koitharu.pausingcoroutinedispatcher.internal.PausingDispatchQueue
import org.koitharu.pausingcoroutinedispatcher.internal.PausingDispatcherImpl
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Launches a new coroutine with an ability to pause it
 * @see launch
 */
public fun CoroutineScope.launchPausing(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): PausingJob {
    val dispatcher = PausingDispatcher(this, context)
    val job = launch(context + dispatcher.queue + dispatcher, start, block)
    return PausingJob(job, dispatcher.queue)
}

/**
 * Launches a new coroutine with an ability to pause it
 * @see async
 */
public fun <T> CoroutineScope.asyncPausing(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
): PausingDeferred<T> {
    val dispatcher = PausingDispatcher(this, context)
    val deferred = async(context + dispatcher.queue + dispatcher, start, block)
    return PausingDeferred(deferred, dispatcher.queue)
}

/**
 * Adjusts a [CoroutineContext] to bring an ability to pausing.
 * Should be used with [withContext], [launch] or [async] inside a pausing context.
 * Under the hood wraps an existing [CoroutineDispatcher] into a [PausingDispatcher].
 * @return new context or [this] if current context is not pausing or already use [PausingDispatcher]
 */
public suspend fun CoroutineContext.pausing(): CoroutineContext {
    val currentContext = currentCoroutineContext()
    val baseDispatcher = this[CoroutineDispatcher]
        ?: currentContext[CoroutineDispatcher]
        ?: Dispatchers.Default
    if (baseDispatcher is PausingDispatcher) {
        return this
    }
    val queue = this[PausingDispatchQueue]
        ?: currentContext[PausingDispatchQueue]
        ?: return this
    return this + PausingDispatcherImpl(queue, baseDispatcher)
}

/**
 * Suspend execution if current coroutine is paused.
 */
public suspend fun ensureNotPaused() {
    val currentContext = currentCoroutineContext()
    currentContext.ensureActive()
    val queue = currentContext[PausingDispatchQueue] ?: return
    if (queue.isPaused) {
        yield()
    }
}

private fun PausingDispatcher(scope: CoroutineScope, newContext: CoroutineContext): PausingDispatcherImpl {
    val dispatcher = newContext[CoroutineDispatcher]
        ?: scope.coroutineContext[CoroutineDispatcher]
        ?: Dispatchers.Default
    return PausingDispatcherImpl(
        queue = PausingDispatchQueue(),
        baseDispatcher = if (dispatcher is PausingDispatcherImpl) dispatcher.baseDispatcher else dispatcher
    )
}