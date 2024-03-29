package com.ryanjames.composemobileordering.core

import com.ryanjames.composemobileordering.domain.AppError
import com.ryanjames.composemobileordering.network.model.Event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf


sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>() {
        val event = Event(data)
    }

    sealed class Error(val throwable: Throwable) : Resource<Nothing>() {
        val event = Event(throwable)

        data class Custom(private val t: Throwable, val error: AppError) : Error(throwable = t)
        data class Generic(private val t: Throwable, val message: String? = null) : Error(throwable = t)
    }

    object Loading : Resource<Nothing>()

    inline fun <K> mapIfSuccess(func: (T) -> K): K? {
        if (this is Success) {
            return func.invoke(this.data)
        }
        return null
    }
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