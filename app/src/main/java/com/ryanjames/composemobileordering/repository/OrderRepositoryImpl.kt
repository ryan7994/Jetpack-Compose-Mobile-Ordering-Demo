package com.ryanjames.composemobileordering.repository

import android.util.Log
import com.ryanjames.composemobileordering.TAG
import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.db.AppDatabase
import com.ryanjames.composemobileordering.domain.LineItem
import com.ryanjames.composemobileordering.domain.OrderStatus
import com.ryanjames.composemobileordering.domain.OrderSummary
import com.ryanjames.composemobileordering.domain.OrderSummaryLineItem
import com.ryanjames.composemobileordering.features.bag.OrderMode
import com.ryanjames.composemobileordering.network.MobilePosApi
import com.ryanjames.composemobileordering.network.model.request.CheckoutOrderRequest
import com.ryanjames.composemobileordering.network.model.request.CreateUpdateOrderRequest
import com.ryanjames.composemobileordering.network.model.request.GetOrderRequest
import com.ryanjames.composemobileordering.network.networkAndDomainResourceFlow
import com.ryanjames.composemobileordering.replaceOrAdd
import com.ryanjames.composemobileordering.util.toBagSummary
import com.ryanjames.composemobileordering.util.toDomain
import com.ryanjames.composemobileordering.util.toLineItemRequest
import com.ryanjames.composemobileordering.util.toOrderEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class OrderRepositoryImpl(
    private val mobilePosApi: MobilePosApi,
    private val roomDb: AppDatabase
) : OrderRepository {


    override fun addOrUpdateLineItem(lineItem: LineItem, venueId: String) = flow {
        emit(Resource.Loading)

        try {
            val currentOrderId = getCurrentOrderId()
            if (currentOrderId == null) {

                val lineItems = listOf(lineItem.toLineItemRequest())
                val getOrderResponse =
                    mobilePosApi.postOrder(CreateUpdateOrderRequest(orderId = null, lineItems = lineItems, status = null, customerName = null, storeId = venueId))
                roomDb.orderDao().updateLocalBag(getOrderResponse.toOrderEntity())
                emit(Resource.Success(getOrderResponse.toBagSummary()))
                roomDb.globalDao().createLocalBagOrderId(orderId = getOrderResponse.orderId, venueId = venueId)
            } else {
                val lineItems = roomDb.orderDao().getAllLineItems()
                val newLineItem = lineItem.toLineItemRequest()
                val newLineItemListRequest = lineItems.map { it.toLineItemRequest() }.replaceOrAdd(newValue = newLineItem) { it.lineItemId == newLineItem.lineItemId }
                val getOrderResponse =
                    mobilePosApi.putOrder(
                        CreateUpdateOrderRequest(
                            orderId = currentOrderId,
                            lineItems = newLineItemListRequest,
                            status = null,
                            customerName = null,
                            storeId = venueId
                        )
                    )
                roomDb.orderDao().updateLocalBag(getOrderResponse.toOrderEntity())
                emit(Resource.Success(getOrderResponse.toBagSummary()))

            }
        } catch (t: Throwable) {
            emit(Resource.Error.Generic(t))
            t.printStackTrace()
        }
    }.flowOn(Dispatchers.IO)

    override fun removeLineItems(lineItemIds: List<String>, venueId: String): Flow<Resource<OrderSummary>> = flow<Resource<OrderSummary>> {
        emit(Resource.Loading)

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
                val bagSummary = getOrderResponse.toBagSummary()
                emit(Resource.Success(bagSummary))
                if (bagSummary.lineItems.isEmpty()) {
                    roomDb.globalDao().clearLocalBag()
                }
            }
        } catch (t: Throwable) {
            emit(Resource.Error.Generic(t))
            Log.e(TAG, t.message, t)
        }

    }.flowOn(Dispatchers.IO)

    override suspend fun retrieveCurrentOrder(): Flow<Resource<OrderSummary>> = channelFlow {
        send(Resource.Loading)
        try {
            val orderId = getCurrentOrderId()
            if (orderId != null) {
                val response = mobilePosApi.getOrder(GetOrderRequest(orderId))
                roomDb.orderDao().updateLocalBag(response.toOrderEntity())
                send(Resource.Success(response.toBagSummary()))
            }

        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            send(Resource.Error.Generic(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getBagSummaryFlow(): Flow<OrderSummary?> {

        return roomDb.orderDao().getCurrentOrderFlow().map { currentOrder ->
            if (currentOrder == null || currentOrder.lineItems.isEmpty()) {
                null
            } else {
                val lineItems = currentOrder.lineItems.map { it.toDomain() }
                OrderSummary(
                    lineItems = lineItems,
                    price = currentOrder.order.total,
                    status = OrderStatus.CREATED,
                    orderId = currentOrder.order.orderId,
                    storeId = currentOrder.order.storeId,
                    storeName = currentOrder.order.storeName
                )
            }
        }
    }

    override suspend fun getLineItems(): List<OrderSummaryLineItem> {
        return roomDb.orderDao().getAllLineItems().map { it.toDomain() }
    }

    private suspend fun getCurrentOrderId(): String? {
        return roomDb.globalDao().getGlobalValues()?.currentOrderId
    }

    override suspend fun clearBag() {
        roomDb.orderDao().clearLocalBag()
        roomDb.globalDao().clearCurrentOrder()
    }

    override fun getDeliveryAddressFlow(): Flow<String?> {
        return roomDb.globalDao().getGlobalValuesFlow().map { it?.deliveryAddress }
    }

    override suspend fun updateDeliveryAddress(address: String?) {
        roomDb.globalDao().createGlobalEntityWithAddress(address)
        val a = if (address?.isNotBlank() == true) address else null
        roomDb.globalDao().updateDeliveryAddress(a)
    }

    override fun checkoutOrder(orderMode: OrderMode): Flow<Resource<OrderSummary>> = networkAndDomainResourceFlow(

        fetchFromApi = {
            val orderId = getCurrentOrderId() ?: throw java.lang.Exception("No order id")
            val isPickup = orderMode is OrderMode.Pickup
            val deliveryAddress = (orderMode as? OrderMode.Delivery)?.deliveryAddress.orEmpty()
            mobilePosApi.checkoutOrder(CheckoutOrderRequest(orderId = orderId, pickup = isPickup, deliveryAddress)).also {
                clearBag()
            }
        },
        mapToDomainModel = { it.toBagSummary() }
    )
}