package com.ryanjames.jetpackmobileordering.core

import com.ryanjames.jetpackmobileordering.network.model.Event


sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>() {
        val event = Event(data)
    }

    data class Error(val throwable: Throwable) : Resource<Nothing>() {
        val event = Event(throwable)
    }

    object Loading : Resource<Nothing>()

    inline fun doOnLoading(func: () -> Unit) {
        if (this is Loading) {
            func.invoke()
        }
    }

    inline fun doOnSuccess(func: (T) -> Unit): T? {
        if (this is Success) {
            func.invoke(this.data)
            return this.data
        }
        return null
    }

    inline fun doOnError(func: (t: Throwable) -> Unit) {
        if (this is Error) {
            func.invoke(this.throwable)
        }
    }

    inline fun <K> mapIfSuccess(func: (T) -> K): K? {
        if (this is Success) {
            return func.invoke(this.data)
        }
        return null
    }
}