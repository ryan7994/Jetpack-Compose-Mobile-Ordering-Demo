package com.ryanjames.composemobileordering

import com.ryanjames.composemobileordering.core.Resource
import kotlinx.coroutines.flow.Flow
import java.util.*

fun Float.toTwoDigitString(): String {
    return String.format(Locale.US, "%.2f", this)
}

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }

fun <T> MutableList<T>.clearAndAddAll(c: Collection<T>) {
    clear()
    addAll(c)
}

fun <T> List<T>.replaceOrAdd(newValue: T, block: (T) -> Boolean): List<T> {
    if (find { block(it) } == null) {
        return this.plus(newValue)
    }
    return map {
        if (block(it)) newValue else it
    }
}

suspend fun <K, T : Resource<K>> Flow<T>.collectResource(
    onLoading: suspend () -> Unit,
    onSuccess: suspend (K) -> Unit,
    onError: suspend (error: Resource.Error) -> Unit
) {
    this.collect { resource ->
        resource.onError {
            onError(it)
        }.onSuccess {
            onSuccess(it)
        }.onLoading {
            onLoading()
        }
    }
}
