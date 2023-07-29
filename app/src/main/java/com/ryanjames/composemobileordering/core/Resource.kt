package com.ryanjames.composemobileordering.core

import com.ryanjames.composemobileordering.domain.AppError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf


sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    sealed class Error(val throwable: Throwable) : Resource<Nothing>() {
        data class Custom(private val t: Throwable, val error: AppError) : Error(throwable = t)
        data class Generic(private val t: Throwable, val message: String? = null) : Error(throwable = t)
    }

    object Loading : Resource<Nothing>()

    inline fun onLoading(func: () -> Unit): Resource<T> {
        if (this is Loading) {
            func()
        }
        return this
    }

    inline fun onSuccess(func: (T) -> Unit): Resource<T> {
        if (this is Success) {
            func(this.data)
        }
        return this
    }

    inline fun onError(func: (error: Error) -> Unit): Resource<T> {
        if (this is Error) {
            func(this)
        }
        return this
    }
}

fun <T> Resource<T>.getOrNull(): T? {
    if (this is Resource.Success) {
        return this.data
    }
    return null
}

@OptIn(ExperimentalCoroutinesApi::class)
inline fun <T, K> Flow<Resource<T>>.flatMapLatest(crossinline transform: suspend (T) -> Flow<Resource<K>>): Flow<Resource<K>> {
    return this.flatMapLatest { resource ->
        when (resource) {
            is Resource.Error.Custom -> flowOf(Resource.Error.Custom(resource.throwable, resource.error))
            is Resource.Error.Generic -> flowOf(Resource.Error.Generic(resource.throwable, resource.message))
            Resource.Loading -> flowOf(Resource.Loading)
            is Resource.Success -> transform.invoke(resource.data)
        }
    }
}