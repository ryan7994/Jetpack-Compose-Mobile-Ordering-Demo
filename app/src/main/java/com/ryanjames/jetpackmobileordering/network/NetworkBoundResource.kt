package com.ryanjames.jetpackmobileordering.network

import com.ryanjames.jetpackmobileordering.core.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
inline fun <DatabaseModel, NetworkModel, DomainModel> networkBoundResource(
    crossinline queryDb: () -> Flow<DatabaseModel>,
    crossinline fetchFromApi: suspend () -> NetworkModel,
    crossinline savetoDb: suspend (NetworkModel) -> Unit,
    crossinline shouldFetchFromApi: (DatabaseModel) -> Boolean = { true },
    crossinline mapToDomainModel: (DatabaseModel) -> DomainModel,
    crossinline onFetchFailed: (Throwable) -> Unit = { }
) = channelFlow {
    val data = queryDb().first()

    if (shouldFetchFromApi(data)) {
        val loading = launch {
            queryDb().collect { send(Resource.Loading) }
        }

        try {
            val networkModel = fetchFromApi()
            savetoDb(networkModel)
            loading.cancel()
            queryDb().collect {
                val domainModel = mapToDomainModel(it)
                send(Resource.Success(domainModel))
            }

        } catch (t: Throwable) {
            onFetchFailed(t)
            t.printStackTrace()
            loading.cancel()
            send(Resource.Error(t))
        }
    } else {
        queryDb().collect { send(Resource.Success(mapToDomainModel(it))) }
    }
}

@ExperimentalCoroutinesApi
inline fun <NetworkModel, DomainModel> networkResource(
    crossinline fetchFromApi: suspend () -> NetworkModel,
    crossinline mapToDomainModel: (NetworkModel) -> DomainModel,
    crossinline onFetchSuccess: (DomainModel) -> Unit = { },
    crossinline onFetchFailed: (Throwable) -> Unit = { }
) = channelFlow {

    val loading = launch {
        send(Resource.Loading)
    }

    try {
        val networkModel = fetchFromApi()
        onFetchSuccess(mapToDomainModel.invoke(networkModel))
        loading.cancel()
        send(Resource.Success(mapToDomainModel.invoke(networkModel)))

    } catch (t: Throwable) {
        onFetchFailed(t)
        t.printStackTrace()
        loading.cancel()
        send(Resource.Error(t))
    }


}