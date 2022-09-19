package org.koitharu.pausingcoroutinedispatcher

import kotlinx.coroutines.CoroutineDispatcher

abstract class PausingDispatcher : CoroutineDispatcher() {

    override fun toString(): String {
        return "PausingDispatcher"
    }
}