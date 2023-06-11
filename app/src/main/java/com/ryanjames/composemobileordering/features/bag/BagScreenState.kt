package com.ryanjames.composemobileordering.features.bag

import com.google.android.gms.maps.model.LatLng

data class BagScreenState(
    val bagItems: List<BagItemRowDisplayModel>,
    val venueId: String?,
    val venueName: String?,
    val bagListMode: BagListMode,
    val priceBreakdownState: PriceBreakdownState,
    val restaurantPosition: LatLng = LatLng(0.0, 0.0),
    val venueAddress: String = "",
    val isPickupSelected: Boolean = true,
    val deliveryAddress: String? = null,
    val isLoading: Boolean = true
) {
    val isBagEmpty
        get() = bagItems.isEmpty()

    val btnRemoveState: ButtonState
        get() = ButtonState(enabled = bagListMode == BagListMode.Viewing, visible = bagListMode == BagListMode.Viewing)

    val btnCancelState: ButtonState
        get() = ButtonState(enabled = bagListMode == BagListMode.Removing, visible = bagListMode == BagListMode.Removing)

    val btnRemoveSelectedState: ButtonState
        get() = ButtonState(enabled = bagItems.any { it.forRemoval }, visible = bagListMode == BagListMode.Removing)
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

enum class DeliveryOption {
    Pickup,
    Delivery
}

enum class BagListMode {
    Viewing,
    Removing
}