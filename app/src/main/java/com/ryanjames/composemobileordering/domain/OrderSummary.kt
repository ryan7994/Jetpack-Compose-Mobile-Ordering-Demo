package com.ryanjames.composemobileordering.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderSummary(
    val lineItems: List<OrderSummaryLineItem>,
    val price: Float,
    val status: OrderStatus,
    val orderId: String,
    val storeId: String,
    val storeName: String
) : Parcelable {

    val subtotal: Float
        get() = price / 1.12f

    val tax: Float
        get() = subtotal * .12f

    companion object {

        val EMPTY = OrderSummary(
            lineItems = listOf(),
            price = 0f,
            status = OrderStatus.UNKNOWN,
            orderId = "",
            storeId = "",
            storeName = ""
        )
    }
}

@Parcelize
data class OrderSummaryLineItem(
    val lineItemId: String,
    val productId: String,
    val bundleId: String?,
    val lineItemName: String,
    val modifiersDisplay: String,
    val price: Float,
    val productsInBundle: HashMap<String, List<String>>,
    val modifiers: HashMap<ProductIdModifierGroupIdKey, List<String>>,
    val quantity: Int
) : Parcelable

@Parcelize
data class ProductIdModifierGroupIdKey(val productId: String, val modifierGroupId: String) : Parcelable {

    override fun toString(): String {
        return "PRODUCT: ${productId}, MODIFIER_GROUP: $modifierGroupId"
    }
}