package com.ryanjames.composemobileordering.domain

import android.os.Parcelable
import com.ryanjames.composemobileordering.features.bag.BagScreenState
import com.ryanjames.composemobileordering.features.bag.ButtonState
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

    // TODO: Replace with API value
    fun tax(): Float {
        return subtotal() * .12f
    }

    // TODO: Replace with API value
    fun subtotal(): Float {
        return price / 1.12f
    }

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