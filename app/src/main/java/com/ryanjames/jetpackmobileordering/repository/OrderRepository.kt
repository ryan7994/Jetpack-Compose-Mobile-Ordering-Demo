package com.ryanjames.jetpackmobileordering.repository

import com.ryanjames.jetpackmobileordering.core.Resource
import com.ryanjames.jetpackmobileordering.db.AppDatabase
import com.ryanjames.jetpackmobileordering.domain.BagLineItem
import com.ryanjames.jetpackmobileordering.domain.BagSummary
import com.ryanjames.jetpackmobileordering.domain.LineItem
import com.ryanjames.jetpackmobileordering.domain.OrderStatus
import com.ryanjames.jetpackmobileordering.network.MobilePosApi
import com.ryanjames.jetpackmobileordering.network.model.CreateUpdateOrderRequest
import com.ryanjames.jetpackmobileordering.network.model.GetOrderRequest
import com.ryanjames.jetpackmobileordering.replaceOrAdd
import com.ryanjames.jetpackmobileordering.ui.toBagSummary
import com.ryanjames.jetpackmobileordering.ui.toDomain
import com.ryanjames.jetpackmobileordering.ui.toLineItemRequest
import com.ryanjames.jetpackmobileordering.ui.toOrderEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import java.util.*

class OrderRepository(
    private val mobilePosApi: MobilePosApi,
    val roomDb: AppDatabase
) : AbsOrderRepository {

    @ExperimentalCoroutinesApi
    override fun addOrUpdateLineItem(lineItem: LineItem, venueId: String): Flow<Resource<BagSummary>> = channelFlow {
        send(Resource.Loading)

        try {
            val currentOrderId = getCurrentOrderId()
            if (currentOrderId == null) {
                val orderId = UUID.randomUUID().toString()
                roomDb.globalDao().createLocalBagOrderId(orderId = orderId, venueId = venueId)
                val lineItems = listOf(lineItem.toLineItemRequest())
                val getOrderResponse = mobilePosApi.postOrder(CreateUpdateOrderRequest(orderId = orderId, lineItems = lineItems, status = null, customerName = null, storeId = venueId))
                roomDb.orderDao().updateLocalBag(getOrderResponse.toOrderEntity())
                send(Resource.Success(getOrderResponse.toBagSummary()))
            } else {
                val lineItems = roomDb.orderDao().getAllLineItems()
                val newLineItem = lineItem.toLineItemRequest()
                val newLineItemListRequest = lineItems.map { it.toLineItemRequest() }.replaceOrAdd(newValue = newLineItem) { it.lineItemId == newLineItem.lineItemId }
                val getOrderResponse =
                    mobilePosApi.putOrder(CreateUpdateOrderRequest(orderId = currentOrderId, lineItems = newLineItemListRequest, status = null, customerName = null, storeId = venueId))
                roomDb.orderDao().updateLocalBag(getOrderResponse.toOrderEntity())
                send(Resource.Success(getOrderResponse.toBagSummary()))

            }
        } catch (t: Throwable) {
            send(Resource.Error(t))
            t.printStackTrace()
        }
    }

    @ExperimentalCoroutinesApi
    override fun removeLineItems(lineItemIds: List<String>, venueId: String): Flow<Resource<BagSummary>> = channelFlow {
        send(Resource.Loading)

        try {
            val currentOrderId = getCurrentOrderId()
            if (currentOrderId != null) {
                val lineItems = roomDb.orderDao().getAllLineItems().toMutableList()
                lineItems.removeIf { lineItemIds.contains(it.lineItem.lineItemId) }
                val getOrderResponse =
                    mobilePosApi.putOrder(
                        CreateUpdateOrderRequest(
                            orderId = currentOrderId,
                            lineItems = lineItems.map { it.toLineItemRequest() },
                            status = null,
                            customerName = null,
                            storeId = venueId
                        )
                    )
                roomDb.orderDao().updateLocalBag(getOrderResponse.toOrderEntity())
                send(Resource.Success(getOrderResponse.toBagSummary()))
            }
        } catch (t: Throwable) {
            send(Resource.Error(t))
            t.printStackTrace()
        }

    }

    override suspend fun retrieveCurrentOrder(): Flow<Resource<BagSummary>> = channelFlow {
        send(Resource.Loading)
        try {
            val orderId = getCurrentOrderId()
            if (orderId != null) {
                val response = mobilePosApi.getOrder(GetOrderRequest(orderId))
                roomDb.orderDao().updateLocalBag(response.toOrderEntity())
                send(Resource.Success(response.toBagSummary()))
            }

        } catch (e: Exception) {
            e.printStackTrace()
            send(Resource.Error(e))
        }
    }

    override fun getBagSummaryFlow(): Flow<BagSummary?> {

        return roomDb.orderDao().getCurrentOrderFlow().map { currentOrder ->
            if (currentOrder == null || currentOrder.lineItems.isNullOrEmpty()) {
                null
            } else {
                val lineItems = currentOrder.lineItems.map { it.toDomain() }
                BagSummary(
                    lineItems = lineItems,
                    price = currentOrder.order.total,
                    status = OrderStatus.CREATED,
                    orderId = currentOrder.order.orderId
                )
            }
        }
    }

    override suspend fun getLineItems(): List<BagLineItem> {
        return roomDb.orderDao().getAllLineItems().map { it.toDomain() }
    }

    private suspend fun getCurrentOrderId(): String? {
        return roomDb.globalDao().getGlobalValues()?.currentOrderId
    }
}