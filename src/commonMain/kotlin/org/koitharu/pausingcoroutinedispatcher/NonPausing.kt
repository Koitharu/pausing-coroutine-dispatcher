package org.koitharu.pausingcoroutinedispatcher

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * Coroutine context element for disable pausing in a part of coroutine
 * ```
 * withContext(NonPausing) {
 *  doSomethingWithoutPausing()
 * }
 * ```
 * @see [withContext]
 */
public object NonPausing : AbstractCoroutineContextElement(Key) {

    private object Key : CoroutineContext.Key<NonPausing>
}