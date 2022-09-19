package org.koitharu.pausingcoroutinedispatcher.internal
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import org.koitharu.pausingcoroutinedispatcher.PausingHandle
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.Volatile

internal class PausingDispatchQueue : AbstractCoroutineContextElement(Key), PausingHandle {

    @Volatile
    private var paused = false
    private val queue = ArrayDeque<Resumer>()
    private val pauseSync = SynchronizedObject()

    override val isPaused: Boolean
        get() = paused

    override fun pause() {
        synchronized(pauseSync) {
            paused = true
        }
    }

    override fun resume() {
        synchronized(pauseSync) {
            if (paused) {
                paused = false
                dispatchNext()
            }
        }
    }

    fun queue(context: CoroutineContext, block: Runnable, dispatcher: CoroutineDispatcher) {
        queue.addLast(Resumer(dispatcher, context, block))
    }

    private fun dispatchNext() {
        val resumer = queue.removeFirstOrNull() ?: return
        resumer.dispatch()
    }

    override fun toString(): String {
        return "PausingDispatchQueue@${hashCode()}"
    }

    private inner class Resumer(
        private val dispatcher: CoroutineDispatcher,
        private val context: CoroutineContext,
        private val block: Runnable,
    ) : Runnable {

        override fun run() {
            block.run()
            if (!paused) {
                dispatchNext()
            }
        }

        fun dispatch() {
            dispatcher.dispatch(context, this)
        }
    }

    companion object Key : CoroutineContext.Key<PausingDispatchQueue>
}