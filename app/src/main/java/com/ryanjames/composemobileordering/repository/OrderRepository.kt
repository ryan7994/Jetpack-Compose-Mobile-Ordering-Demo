package com.ryanjames.composemobileordering.repository

import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.domain.BagLineItem
import com.ryanjames.composemobileordering.domain.BagSummary
import com.ryanjames.composemobileordering.domain.LineItem
import kotlinx.coroutines.flow.Flow

interface OrderRepository {

    fun addOrUpdateLineItem(lineItem: LineItem, venueId: String): Flow<Resource<BagSummary>>

    fun getBagSummaryFlow(): Flow<BagSummary?>

    suspend fun getLineItems(): List<BagLineItem>

    fun removeLineItems(lineItemIds: List<String>, venueId: String): Flow<Resource<BagSummary>>

    suspend fun retrieveCurrentOrder(): Flow<Resource<BagSummary>>

    suspend fun clearBag()

    fun getDeliveryAddressFlow(): Flow<String?>

    suspend fun updateDeliveryAddress(address: String?)
}