package org.koitharu.pausingcoroutinedispatcher

import kotlinx.coroutines.Job

class PausingJob(
    private val job: Job,
    private val pausingHandle: PausingHandle,
) : Job by job, PausingHandle by pausingHandle