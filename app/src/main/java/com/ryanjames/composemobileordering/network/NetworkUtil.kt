package com.ryanjames.composemobileordering.network

import android.util.Log
import com.google.gson.Gson
import com.ryanjames.composemobileordering.TAG
import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.network.model.response.ApiErrorResponse
import com.ryanjames.composemobileordering.util.toDomain
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException


/**
 * 1. Creates a flow that queries the database first before making an API call.
 * 2. When the API call is successful, data is saved to the database.
 * 3. After saving to the database, database model is converted to its domain model and sends the domain data to its collectors.
 *
 * @param queryDb - function that returns a flow to get data from database
 * @param fetchFromApi - function that returns a flow to get data from the API
 * @param saveToDb - function that takes a network model to save to the database
 * @param shouldFetchFromApi - function that takes a database model and returns a boolean that determines whether or not to fetch data from API
 * @param mapDbToDomainModel - function that converts database model to its domain model
 * @param onFetchFailed - invoked when operation failed
 */

inline fun <DatabaseModel, NetworkModel, DomainModel> networkBoundResourceFlow(
    crossinline queryDb: () -> Flow<DatabaseModel>,
    crossinline fetchFromApi: suspend () -> NetworkModel,
    crossinline saveToDb: suspend (NetworkModel) -> Unit,
    crossinline shouldFetchFromApi: (DatabaseModel) -> Boolean = { true },
    crossinline mapDbToDomainModel: (DatabaseModel) -> DomainModel,
    crossinline onFetchFailed: (Throwable) -> Unit = { }
): Flow<Resource<DomainModel>> = channelFlow {
    val data = queryDb().first()
    send(Resource.Success(mapDbToDomainModel.invoke(data)))

    if (shouldFetchFromApi(data)) {
        val loading = launch {
            queryDb().collect { send(Resource.Loading) }
        }

        try {
            val networkModel = fetchFromApi()
            saveToDb(networkModel)
            loading.cancel()
            queryDb().collect {
                val domainModel = mapDbToDomainModel(it)
                send(Resource.Success(domainModel))
            }

        } catch (t: Throwable) {
            onFetchFailed(t)
            t.printStackTrace()
            loading.cancel()
            send(getResourceError(t))
        }
    } else {
        queryDb().collect { send(Resource.Success(mapDbToDomainModel(it))) }
    }
}

inline fun <NetworkModel, DomainModel> networkAndDomainResourceFlow(
    crossinline fetchFromApi: suspend () -> NetworkModel,
    crossinline mapToDomainModel: (NetworkModel) -> DomainModel,
    crossinline onFetchSuccess: (DomainModel) -> Unit = { },
    crossinline onFetchFailed: (Throwable) -> Unit = { }
): Flow<Resource<DomainModel>> = channelFlow {

    val loading = launch {
        send(Resource.Loading)
    }

    try {
        val networkModel = fetchFromApi()
        onFetchSuccess(mapToDomainModel.invoke(networkModel))
        loading.cancel()
        send(Resource.Success(mapToDomainModel.invoke(networkModel)))

    } catch (t: Throwable) {
        Log.e(TAG, t.message, t)
        loading.cancel()
        onFetchFailed(t)
        send(getResourceError(t))
    }
}

inline fun <NetworkModel> networkOnlyResourceFlow(
    crossinline apiCall: suspend () -> NetworkModel
//    crossinline onSuccess: (suspend (ProducerScope<Resource<NetworkModel>>, NetworkModel) -> Boolean)
): Flow<Resource<NetworkModel>> = channelFlow {
    try {
        send(Resource.Loading)
        val response = apiCall.invoke()
        send(Resource.Success(response))
    } catch (t: Throwable) {
        Log.e(TAG, t.message, t)
        send(getResourceError(t))
    }
}

fun <T : Resource.Error> getResourceError(t: Throwable): Resource<T> {
    if (t is HttpException) {
        val code = t.code()

        try {
            val apiErrorResponse: ApiErrorResponse? = Gson().fromJson(t.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            return if (apiErrorResponse?.message != null) {
                Resource.Error.Api(t, apiErrorResponse.toDomain(code))
            } else {
                Resource.Error.Generic(Exception("Json is empty or null"), "")
            }
        } catch (e: Exception) {
            Resource.Error.Generic(e, e.message)
        }
    }
    return Resource.Error.Generic(Exception(), "")
}