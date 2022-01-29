package com.ryanjames.jetpackmobileordering.features.bag

import com.ryanjames.jetpackmobileordering.ui.core.AlertDialogState

data class BagScreenState(
    val bagItems: List<BagItemRowDisplayModel>,
    val venueId: String?,
    val venueName: String?,
    val btnRemoveState: ButtonState,
    val btnCancelState: ButtonState,
    val btnRemoveSelectedState: ButtonState,
    val isRemoving: Boolean,
    val alertDialog: AlertDialogState?
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