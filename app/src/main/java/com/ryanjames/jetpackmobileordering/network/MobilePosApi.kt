package com.ryanjames.jetpackmobileordering.network

import com.ryanjames.jetpackmobileordering.network.model.*
import retrofit2.http.*

interface MobilePosApi {

    @POST("/login")
    @Headers("No-Authentication: true")
    suspend fun login(@Body loginRequestBody: LoginRequestBody): LoginResponse

    @POST("/refresh")
    suspend fun refresh(): RefreshTokenResponse

    @GET("/home")
    suspend fun getFeaturedVenues(): HomeResponse

    @GET("/store/{venue_id}")
    suspend fun getVenueById(@Path(value = "venue_id", encoded = true) venueId: String): VenueResponse

    @GET("/basicmenu/{venue_id}")
    suspend fun getBasicMenuByVenue(@Path(value = "venue_id", encoded = true) venueId: String): BasicMenuResponse

    @GET("/productDetail/{product_id}")
    suspend fun getProductDetails(@Path(value = "product_id", encoded = true) productId: String): ProductDetailsResponse

    @POST("/order")
    suspend fun postOrder(@Body createUpdateOrderRequest: CreateUpdateOrderRequest): GetOrderResponse

    @PUT("/order")
    suspend fun putOrder(@Body createUpdateOrderRequest: CreateUpdateOrderRequest): GetOrderResponse

    @POST("/retrieveOrder")
    suspend fun getOrder(@Body getOrderRequest: GetOrderRequest): GetOrderResponse
}