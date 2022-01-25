package com.ryanjames.jetpackmobileordering.repository

import com.ryanjames.jetpackmobileordering.core.Resource
import com.ryanjames.jetpackmobileordering.db.AppDatabase
import com.ryanjames.jetpackmobileordering.domain.BagLineItem
import com.ryanjames.jetpackmobileordering.domain.BagSummary
import com.ryanjames.jetpackmobileordering.domain.LineItem
import com.ryanjames.jetpackmobileordering.network.MobilePosApi
import com.ryanjames.jetpackmobileordering.network.model.CreateUpdateOrderRequest
import com.ryanjames.jetpackmobileordering.replaceOrAdd
import com.ryanjames.jetpackmobileordering.ui.toBagSummary
import com.ryanjames.jetpackmobileordering.ui.toDomain
import com.ryanjames.jetpackmobileordering.ui.toEntity
import com.ryanjames.jetpackmobileordering.ui.toLineItemRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import java.util.*

class OrderRepository(
    private val mobilePosApi: MobilePosApi,
    val roomDb: AppDatabase
) {

    @ExperimentalCoroutinesApi
    fun addOrUpdateLineItem(lineItem: LineItem, venueId: String): Flow<Resource<BagSummary>> = channelFlow {
        send(Resource.Loading)

        try {
            val currentOrderId = getCurrentOrderId()
            if (currentOrderId == null) {
                val orderId = UUID.randomUUID().toString()
                roomDb.globalDao().createLocalBagOrderId(orderId = orderId, venueId = venueId)
                val lineItems = listOf(lineItem.toLineItemRequest())
                val getOrderResponse = mobilePosApi.postOrder(CreateUpdateOrderRequest(orderId = orderId, lineItems = lineItems, status = null, customerName = null, storeId = venueId))
                roomDb.orderDao().updateLocalBag(getOrderResponse.toEntity(), venueId)
                send(Resource.Success(getOrderResponse.toBagSummary()))
            } else {
                val lineItems = roomDb.orderDao().getAllLineItems()
                val newLineItem = lineItem.toLineItemRequest()
                val newLineItemListRequest = lineItems.map { it.toLineItemRequest() }.replaceOrAdd(newValue = newLineItem) { it.lineItemId == newLineItem.lineItemId }
                val getOrderResponse = mobilePosApi.putOrder(CreateUpdateOrderRequest(orderId = currentOrderId, lineItems = newLineItemListRequest, status = null, customerName = null, storeId = venueId))
                roomDb.orderDao().updateLocalBag(getOrderResponse.toEntity(), venueId)
                send(Resource.Success(getOrderResponse.toBagSummary()))

            }
        } catch (t: Throwable) {
            send(Resource.Error(t))
            t.printStackTrace()
        }
    }

    fun getLineItemsFlow(): Flow<List<BagLineItem>> {
        return roomDb.orderDao().getAllLineItemsFlow().map { list -> list.map { it.toDomain() } }
    }

    private suspend fun getCurrentOrderId(): String? {
        return roomDb.globalDao().getGlobalValues()?.currentOrderId
    }
}