package org.koitharu.pausingcoroutinedispatcher

interface PausingHandle {

    val isPaused: Boolean

    fun pause()

    fun resume()
}