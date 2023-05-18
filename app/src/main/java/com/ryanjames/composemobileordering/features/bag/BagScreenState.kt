package com.ryanjames.composemobileordering.features.bag

import com.google.android.gms.maps.model.LatLng

data class BagScreenState(
    val bagItems: List<BagItemRowDisplayModel>,
    val venueId: String?,
    val venueName: String?,
    val btnRemoveState: ButtonState = ButtonState(enabled = false, visible = true),
    val btnCancelState: ButtonState = ButtonState(enabled = false, visible = true),
    val btnRemoveSelectedState: ButtonState,
    val isRemoving: Boolean,
    val subtotal: String = "",
    val tax: String = "",
    val total: String = "",
    val restaurantPosition: LatLng = LatLng(0.0, 0.0),
    val venueAddress: String = "",
    val isPickupSelected: Boolean = true,
    val deliveryAddress: String? = null,
    val isBagEmpty: Boolean = false,
    val isLoading: Boolean = true
)

data class ButtonState(val enabled: Boolean, val visible: Boolean)

data class BagItemRowDisplayModel(
    val lineItemId: String,
    val qty: String,
    val itemName: String,
    val itemModifier: String,
    val price: String,
    val forRemoval: Boolean
)