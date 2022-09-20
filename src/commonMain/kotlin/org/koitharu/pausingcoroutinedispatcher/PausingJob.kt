package org.koitharu.pausingcoroutinedispatcher

import kotlinx.coroutines.Job

/**
 * Represent a [Job] that also can be paused and resumed
 */
public class PausingJob(
    private val job: Job,
    private val pausingHandle: PausingHandle,
) : Job by job, PausingHandle by pausingHandle