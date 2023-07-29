package com.ryanjames.composemobileordering.features.bag

import com.google.android.gms.maps.model.LatLng

data class BagScreenState(
    val bagItems: List<BagItemRowDisplayModel> = listOf(),
    val venueId: String? = null,
    val venueName: String? = null,
    val bagListMode: BagListMode = BagListMode.VIEWING,
    val priceBreakdownState: PriceBreakdownState = PriceBreakdownState(),
    val orderMode: OrderMode = OrderMode.Pickup(),
    val isLoading: Boolean = true
) {
    val isBagEmpty
        get() = bagItems.isEmpty()

    val btnRemoveState: ButtonState
        get() = ButtonState(enabled = bagListMode == BagListMode.VIEWING, visible = bagListMode == BagListMode.VIEWING)

    val btnCancelState: ButtonState
        get() = ButtonState(enabled = bagListMode == BagListMode.REMOVING, visible = bagListMode == BagListMode.REMOVING)

    val btnRemoveSelectedState: ButtonState
        get() = ButtonState(enabled = bagItems.any { it.forRemoval }, visible = bagListMode == BagListMode.REMOVING)
}

data class ButtonState(val enabled: Boolean, val visible: Boolean)

data class BagItemRowDisplayModel(
    val lineItemId: String,
    val qty: String,
    val itemName: String,
    val itemModifier: String,
    val price: String,
    val forRemoval: Boolean
)

data class PriceBreakdownState(
    val subtotal: String = "",
    val tax: String = "",
    val total: String = ""
)

sealed class OrderMode {
    data class Delivery(val deliveryAddress: String? = null) : OrderMode()
    data class Pickup(
        val venueAddress: String = "",
        val restaurantPosition: LatLng = LatLng(0.0, 0.0),
    ) : OrderMode()
}

enum class OrderModeId {
    PICKUP,
    DELIVERY
}

enum class BagListMode {
    VIEWING,
    REMOVING
}