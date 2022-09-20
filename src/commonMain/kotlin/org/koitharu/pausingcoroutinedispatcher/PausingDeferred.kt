package org.koitharu.pausingcoroutinedispatcher

import kotlinx.coroutines.Deferred

/**
 * Represent a [Deferred] that also can be paused and resumed
 */
public class PausingDeferred<T>(
    private val deferred: Deferred<T>,
    private val pausingHandle: PausingHandle,
) : Deferred<T> by deferred, PausingHandle by pausingHandle