package com.ryanjames.composemobileordering.network

//abstract class BaseService() {
//
//    protected abstract fun parseCustomError(
//        responseMessage: String,
//        responseCode: Int,
//        errorBodyJson: String
//    ): ApiBaseException
//
//    protected suspend fun <T : Any> apiCall(call: suspend () -> Response<T>): ApiResult<T> {
//        val response: Response<T>
//
//        try {
//            response = call.invoke()
//        } catch (t: Throwable) {
//            return ApiResult.Error(NetworkBaseException(t))
//        }
//
//        return if (!response.isSuccessful) {
//            val errorBody = response.errorBody()
//
//            @Suppress("BlockingMethodInNonBlockingContext")
//            ApiResult.Error(
//                parseCustomError(
//                    response.message(),
//                    response.code(),
//                    errorBody?.string() ?: ""
//                )
//            )
//        } else {
//            return response.body()?.let {
//                ApiResult.Success(it)
//            } ?: ApiResult.Error(NullBodyApiException())
//        }
//    }
//
//    protected fun mapHttpThrowable(
//        throwable: Throwable,
//        code: Int,
//        message: String,
//        asad: ApiBaseException
//    ): HttpBaseException {
//        when(asad) {
//            is D -> TODO()
//            is GenericApiException -> TODO()
//            is HttpBaseException -> TODO()
//            is NetworkBaseException -> TODO()
//            is NullBodyApiException -> TODO()
//        }
//        return HttpBaseException(throwable = throwable)
//    }
//}
//
//open class ApiService : BaseService() {
//    override fun parseCustomError(
//        responseMessage: String,
//        responseCode: Int,
//        errorBodyJson: String,
//    ): ApiBaseException {
//
//        return GenericApiException(responseCode, responseMessage, errorBodyJson)
//    }
//}
//
//sealed class ApiResult<out T : Any> {
//    data class Success<out T : Any>(val data: T) : ApiResult<T>()
//    data class Error(val exception: ApiBaseException) : ApiResult<Nothing>()
//}
//
//sealed class ApiBaseException : Exception()
//class NullBodyApiException : ApiBaseException()
//class GenericApiException(val errorCode: Int, responseMessage: String, errorBodyJson: String) : ApiBaseException()
//class NetworkBaseException(val throwable: Throwable) : ApiBaseException()
//class HttpBaseException(val throwable: Throwable) : ApiBaseException()
//
//sealed class Asad: ApiBaseException()
//class D: Asad()
