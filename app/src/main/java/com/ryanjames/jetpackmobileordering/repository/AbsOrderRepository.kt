package com.ryanjames.jetpackmobileordering.repository

import com.ryanjames.jetpackmobileordering.core.Resource
import com.ryanjames.jetpackmobileordering.domain.BagLineItem
import com.ryanjames.jetpackmobileordering.domain.BagSummary
import com.ryanjames.jetpackmobileordering.domain.LineItem
import kotlinx.coroutines.flow.Flow

interface AbsOrderRepository {

    fun addOrUpdateLineItem(lineItem: LineItem, venueId: String): Flow<Resource<BagSummary>>

    fun getBagSummaryFlow(): Flow<BagSummary?>

    suspend fun getLineItems(): List<BagLineItem>

    fun removeLineItems(lineItemIds: List<String>, venueId: String): Flow<Resource<BagSummary>>

    suspend fun retrieveCurrentOrder(): Flow<Resource<BagSummary>>

    suspend fun clearBag()

    fun getDeliveryAddressFlow(): Flow<String?>

    suspend fun updateDeliveryAddress(address: String?)
}