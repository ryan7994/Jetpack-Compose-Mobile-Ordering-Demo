package com.ryanjames.composemobileordering.repository

import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.domain.LineItem
import com.ryanjames.composemobileordering.domain.OrderSummary
import com.ryanjames.composemobileordering.domain.OrderSummaryLineItem
import com.ryanjames.composemobileordering.features.bag.OrderMode
import kotlinx.coroutines.flow.Flow

interface OrderRepository {

    fun addOrUpdateLineItem(lineItem: LineItem, venueId: String): Flow<Resource<OrderSummary>>

    fun getBagSummaryFlow(): Flow<OrderSummary?>

    suspend fun getLineItems(): List<OrderSummaryLineItem>

    fun removeLineItems(lineItemIds: List<String>, venueId: String): Flow<Resource<OrderSummary>>

    suspend fun retrieveCurrentOrder(): Flow<Resource<OrderSummary>>

    suspend fun clearBag()

    fun getDeliveryAddressFlow(): Flow<String?>

    suspend fun updateDeliveryAddress(address: String?)

    fun checkoutOrder(orderMode: OrderMode): Flow<Resource<OrderSummary>>
}