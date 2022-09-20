package org.koitharu.pausingcoroutinedispatcher.internal

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Runnable
import org.koitharu.pausingcoroutinedispatcher.NonPausing
import org.koitharu.pausingcoroutinedispatcher.PausingDispatcher
import kotlin.coroutines.CoroutineContext

internal class PausingDispatcherImpl(
    internal val queue: PausingDispatchQueue,
    internal val baseDispatcher: CoroutineDispatcher,
): PausingDispatcher() {

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (isPaused(context)) {
            queue.queue(context, block, baseDispatcher)
        } else {
            baseDispatcher.dispatch(context, block)
        }
    }

    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        return super.isDispatchNeeded(context) || isPaused(context)
    }

    @InternalCoroutinesApi
    override fun dispatchYield(context: CoroutineContext, block: Runnable) {
        if (isPaused(context)) {
            queue.queue(context, block, baseDispatcher)
        } else {
            baseDispatcher.dispatchYield(context, block)
        }
    }

    @ExperimentalCoroutinesApi
    override fun limitedParallelism(parallelism: Int): CoroutineDispatcher {
        return PausingDispatcherImpl(queue, baseDispatcher.limitedParallelism(parallelism))
    }

    private fun isPaused(context: CoroutineContext): Boolean {
        return queue.isPaused && context[NonPausing.key] == null
    }
}