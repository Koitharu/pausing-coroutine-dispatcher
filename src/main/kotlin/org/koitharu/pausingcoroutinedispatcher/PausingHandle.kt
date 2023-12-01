package org.koitharu.pausingcoroutinedispatcher

/**
 * An entry point to control pausing coroutine execution
 */
public interface PausingHandle {

    /**
     * Returns is coroutine paused or not
     */
    public val isPaused: Boolean

    /**
     * Pause a coroutine with all nested coroutines.
     * Do nothing if already paused.
     * It is safe to call this from any thread
     */
    public fun pause()

    /**
     * Resume a coroutine with all nested coroutines.
     * Do nothing if not paused.
     * It is safe to call this from any thread
     */
    public fun resume()
}