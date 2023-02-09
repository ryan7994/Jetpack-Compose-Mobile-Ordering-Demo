package com.ryanjames.composemobileordering.network

import com.ryanjames.composemobileordering.network.model.request.*
import com.ryanjames.composemobileordering.network.model.response.*
import retrofit2.http.*

interface MobilePosApi {

    @POST("/v1/auth/login")
    @Headers("No-Authentication: true")
    suspend fun login(@Body loginRequestBody: LoginRequestBody): LoginResponse

    @POST("/v1/auth/refresh")
    suspend fun refresh(): RefreshTokenResponse

    @GET("v1/store/home")
    suspend fun getFeaturedVenues(): HomeResponse

    @GET("/store/{venue_id}")
    suspend fun getVenueById(@Path(value = "venue_id", encoded = true) venueId: String): VenueResponse

    @GET("v1/basicmenu/{venue_id}")
    suspend fun getBasicMenuByVenue(@Path(value = "venue_id", encoded = true) venueId: String): BasicMenuResponse

    @GET("v1/productDetail/{product_id}")
    suspend fun getProductDetails(@Path(value = "product_id", encoded = true) productId: String): ProductDetailsResponse

    @POST("v1/order")
    suspend fun postOrder(@Body createUpdateOrderRequest: CreateUpdateOrderRequest): GetOrderResponse

    @PUT("v1/order")
    suspend fun putOrder(@Body createUpdateOrderRequest: CreateUpdateOrderRequest): GetOrderResponse

    @POST("v1/order/cancel")
    suspend fun cancelOrder(@Body cancelOrderRequest: CancelOrderRequest): GetOrderResponse

    @POST("v1/order/checkout")
    suspend fun checkoutOrder(@Body checkoutOrderRequest: CheckoutOrderRequest): GetOrderResponse

    @POST("/retrieveOrder")
    suspend fun getOrder(@Body getOrderRequest: GetOrderRequest): GetOrderResponse

    @POST("/v1/auth/enroll")
    suspend fun enroll(@Body enrollRequest: EnrollRequest): EnrollResponse
}