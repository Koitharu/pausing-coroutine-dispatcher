package org.koitharu.pausingcoroutinedispatcher

import kotlinx.coroutines.Deferred

class PausingDeferred<T>(
    private val deferred: Deferred<T>,
    private val pausingHandle: PausingHandle,
) : Deferred<T> by deferred, PausingHandle by pausingHandle