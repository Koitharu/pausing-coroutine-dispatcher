package org.koitharu.pausingcoroutinedispatcher.internal

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

object NonPausing : AbstractCoroutineContextElement(Key) {

    private object Key : CoroutineContext.Key<NonPausing>
}