package com.ryanjames.composemobileordering.features.bag

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.features.bottomnav.LocalCoroutineScope
import com.ryanjames.composemobileordering.features.bottomnav.LocalSnackbarHostState
import com.ryanjames.composemobileordering.features.common.editdeliveryaddress.DeliveryAddressState
import com.ryanjames.composemobileordering.features.common.editdeliveryaddress.EditDeliveryAddressViewModel
import com.ryanjames.composemobileordering.ui.core.*
import com.ryanjames.composemobileordering.ui.theme.*
import com.ryanjames.composemobileordering.ui.widget.DeliveryAddressBottomSheetLayout
import com.ryanjames.composemobileordering.util.getBitmapDescriptor
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

@FlowPreview
@Composable
fun BagScreen(
    bagViewModel: BagViewModel,
    editDeliveryAddressViewModel: EditDeliveryAddressViewModel,
    onClickAddMoreItems: (venueId: String) -> Unit,
    onClickLineItem: (lineItemId: String) -> Unit,
    onClickBrowseRestaurants: () -> Unit
) {
    BagLayout(
        bagScreenState = bagViewModel.bagScreenState.collectAsState().value,
        deliveryAddressState = editDeliveryAddressViewModel.deliveryAddressState.collectAsState().value,
        onClickAddMoreItems = onClickAddMoreItems,
        onClickLineItem = onClickLineItem,
        onClickRemove = bagViewModel::onClickRemove,
        onClickCancel = bagViewModel::onClickCancel,
        onClickRemoveSelected = bagViewModel::onClickRemoveSelected,
        onCheckChanged = bagViewModel::onRemoveCbCheckChanged,
        onClickPickup = bagViewModel::onClickPickup,
        onClickDelivery = bagViewModel::onClickDelivery,
        onDeliveryAddressValueChange = editDeliveryAddressViewModel::onDeliveryAddressInputChange,
        onClickSaveDeliveryAddress = editDeliveryAddressViewModel::updateDeliveryAddress,
        onClickBrowseRestaurants = onClickBrowseRestaurants,
        onClickCheckout = bagViewModel::onClickCheckout
    )
    val globalScope = LocalCoroutineScope.current
    val snackbarHostState = LocalSnackbarHostState.current
    val snackbarMessage = stringResource(R.string.item_removed)

    LaunchedEffect(Unit) {
        globalScope.launch {
            bagViewModel.onItemRemoval.collect { event ->
                if (event.peekContent()) {
                    event.handleSuspending {
                        snackbarHostState.showSnackbar(snackbarMessage)
                    }
                }

            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BagLayout(
    bagScreenState: BagScreenState,
    deliveryAddressState: DeliveryAddressState,
    onClickAddMoreItems: (venueId: String) -> Unit,
    onClickLineItem: (lineItemId: String) -> Unit,
    onClickRemove: () -> Unit,
    onClickCancel: () -> Unit,
    onClickRemoveSelected: () -> Unit,
    onCheckChanged: (checked: Boolean, lineItemId: String) -> Unit,
    onClickPickup: () -> Unit,
    onClickDelivery: () -> Unit,
    onDeliveryAddressValueChange: (String) -> Unit,
    onClickSaveDeliveryAddress: () -> Unit,
    onClickBrowseRestaurants: () -> Unit,
    onClickCheckout: () -> Unit
) {

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    val scope = rememberCoroutineScope()

    BackHandler(enabled = modalBottomSheetState.isVisible) {
        scope.launch {
            modalBottomSheetState.hide()
        }
    }


    ModalBottomSheetLayout(
        sheetContent = {
            DeliveryAddressBottomSheetLayout(value = deliveryAddressState.deliveryAddressInput, onValueChange = onDeliveryAddressValueChange, onClickSave = {
                onClickSaveDeliveryAddress.invoke()
                scope.launch {
                    modalBottomSheetState.hide()
                }
            })
        },
        sheetState = modalBottomSheetState,
        scrimColor = Color.Transparent,
        sheetBackgroundColor = AppTheme.colors.bottomNavBackground,
        sheetShape = RoundedCornerShape(topEnd = 32.dp, topStart = 32.dp),
        sheetElevation = 8.dp,
    ) {
        Box {

            if (bagScreenState.isLoading) {

            } else if (bagScreenState.isBagEmpty) {
                EmptyBag(onClickBrowseRestaurants)
            } else {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    Spacer(modifier = Modifier.size(16.dp))
                    if (bagScreenState.venueName != null) {
                        Text(
                            text = stringResource(R.string.your_bag_from),
                            style = Typography.bodyMedium,
                            color = AppTheme.colors.darkTextColor
                        )

                        Text(
                            text = bagScreenState.venueName,
                            style = Typography.titleLarge,
                            color = AppTheme.colors.darkTextColor
                        )
                    }

                    OutlinedAccentButton(
                        onClick = {
                            bagScreenState.venueId?.let { onClickAddMoreItems.invoke(it) }
                        },
                        label = stringResource(R.string.add_more_items)
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        if (bagScreenState.btnRemoveState.visible) {
                            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                AccentTextButton(
                                    onClick = {
                                        onClickRemove.invoke()
                                    },
                                    label = stringResource(R.string.remove),
                                    buttonState = bagScreenState.btnRemoveState
                                )
                            }
                        }

                        AccentTextButton(
                            onClick = {
                                onClickRemoveSelected.invoke()
                            },
                            label = stringResource(R.string.remove_selected),
                            buttonState = bagScreenState.btnRemoveSelectedState
                        )

                        AccentTextButton(
                            onClick = {
                                onClickCancel.invoke()
                            },
                            label = stringResource(R.string.cancel),
                            buttonState = bagScreenState.btnCancelState
                        )
                    }

                    BagSummaryCard(bagScreenState.bagItems, onClickLineItem = onClickLineItem, isRemoving = bagScreenState.isRemoving, onCheckChanged = onCheckChanged)
                    Spacer(modifier = Modifier.size(24.dp))
                    DeliveryOptionToggle(bagScreenState.isPickupSelected, onClickPickup = onClickPickup, onClickDelivery = onClickDelivery)
                    Spacer(modifier = Modifier.size(16.dp))

                    if (bagScreenState.isPickupSelected) {
                        MapCard(latLng = bagScreenState.restaurantPosition, bagScreenState.venueAddress)
                    } else {
                        DeliveryAddressCard(deliveryAddress = bagScreenState.deliveryAddress) {
                            scope.launch {
                                modalBottomSheetState.animateTo(ModalBottomSheetValue.Expanded)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.size(24.dp))
                    OrderPriceBreakdown(bagScreenState.subtotal, bagScreenState.tax, bagScreenState.total)
                    Spacer(modifier = Modifier.size(16.dp))
                    FullWidthButton(onClick = onClickCheckout, label = stringResource(id = R.string.checkout))
                    Spacer(modifier = Modifier.size(24.dp))
                }
            }
            Dialog(bagScreenState.alertDialog)
        }
    }


}

@Composable
fun OrderPriceBreakdown(subtotal: String, tax: String, total: String) {
    Column {
        Text(text = stringResource(R.string.summary), style = Typography.titleLarge, color = AppTheme.colors.darkTextColor)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.subtotal),
                style = Typography.bodyLarge,
                color = AppTheme.colors.lightTextColor
            )
            Text(
                text = subtotal,
                style = Typography.bodyLarge,
                color = AppTheme.colors.darkTextColor
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.tax),
                style = Typography.bodyLarge,
                color = AppTheme.colors.lightTextColor
            )
            Text(
                text = tax,
                style = Typography.bodyLarge,
                color = AppTheme.colors.darkTextColor
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        DashedHorizontalLine()
        Spacer(modifier = Modifier.size(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.total),
                style = Typography.titleMedium,
                color = AppTheme.colors.darkTextColor
            )
            Text(
                text = total,
                style = Typography.titleMedium,
                color = AppTheme.colors.darkTextColor
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
    }
}

@Composable
fun BagSummaryCard(
    bagItems: List<BagItemRowDisplayModel>,
    onClickLineItem: (lineItemId: String) -> Unit,
    isRemoving: Boolean,
    onCheckChanged: (checked: Boolean, lineItemId: String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {

        Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
            Text(
                text = stringResource(R.string.select_item_to_modify),
                style = Typography.titleLarge
            )

            bagItems.forEachIndexed { index, bagItemRowState ->
                Column(modifier = Modifier.clickable {
                    onClickLineItem.invoke(bagItemRowState.lineItemId)
                }) {
                    Spacer(modifier = Modifier.size(16.dp))
                    BagItemRow(bagItemRowDisplayModel = bagItemRowState, isRemoving = isRemoving, onCheckChanged = onCheckChanged)
                    Spacer(modifier = Modifier.size(16.dp))
                    if (index < bagItems.size - 1) {
                        HorizontalLine()
                    }
                }

            }
        }

    }
}

@Composable
fun DeliveryOptionToggle(pickupSelected: Boolean, onClickPickup: () -> Unit, onClickDelivery: () -> Unit) {
    val radius = 8.dp
    val selectedColor = ButtonDefaults.outlinedButtonColors(containerColor = CoralRed)
    val unselectedColor = ButtonDefaults.outlinedButtonColors(containerColor = AppTheme.colors.materialColors.surfaceVariant)
    val selectedTextColor = Color.White
    val unselectedTextColor = AppTheme.colors.darkTextColor

    Row(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            colors = if (pickupSelected) selectedColor else unselectedColor,
            shape = RoundedCornerShape(topStart = radius, bottomStart = radius),
            onClick = { onClickPickup.invoke() },
            modifier = Modifier.weight(1f),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Text(
                text = stringResource(R.string.pickup),
                color = if (pickupSelected) selectedTextColor else unselectedTextColor,
                style = Typography.titleLarge
            )
        }
        OutlinedButton(
            colors = if (!pickupSelected) selectedColor else unselectedColor,
            shape = RoundedCornerShape(topEnd = radius, bottomEnd = radius),
            onClick = { onClickDelivery.invoke() },
            modifier = Modifier.weight(1f),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Text(
                text = stringResource(R.string.delivery),
                color = if (!pickupSelected) selectedTextColor else unselectedTextColor,
                style = Typography.titleLarge
            )
        }
    }

}

@Composable
fun MapCard(latLng: LatLng, address: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            GoogleMap(
                modifier = Modifier.height(200.dp),
                cameraPositionState = CameraPositionState(CameraPosition.fromLatLngZoom(latLng, 13f)),
                uiSettings = MapUiSettings(
                    zoomGesturesEnabled = false,
                    zoomControlsEnabled = false,
                    rotationGesturesEnabled = false,
                    scrollGesturesEnabled = false, tiltGesturesEnabled = false
                )
            ) {
                Marker(
                    icon = getBitmapDescriptor(LocalContext.current, R.drawable.marker, 48.dp, 48.dp),
                    state = MarkerState(latLng),
                    title = ".",
                    onClick = {
                        false
                    }
                )
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = stringResource(id = R.string.pickup_from),
                    color = AppTheme.colors.lightTextColor,
                    style = Typography.bodyLarge
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = address,
                    color = AppTheme.colors.darkTextColor,
                    style = Typography.bodyLarge
                )
            }
        }

    }
}

@Composable
fun EmptyBag(onClickBrowseRestaurants: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painter = painterResource(id = R.drawable.online_order), contentDescription = "", modifier = Modifier.size(250.dp))
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = stringResource(R.string.bag_empty), style = Typography.bodyLarge, color = AppTheme.colors.darkTextColor)
        Spacer(modifier = Modifier.size(8.dp))
        OutlinedAccentButton(onClick = { onClickBrowseRestaurants.invoke() }, label = stringResource(R.string.browse_restaurants))
    }

}

@Composable
fun DeliveryAddressCard(deliveryAddress: String?, onClickAddEditAddress: () -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 16.dp)) {
            if (deliveryAddress == null) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.no_address_provided),
                        color = AppTheme.colors.lightTextColor,
                        style = Typography.bodyLarge
                    )
                    AccentTextButton(
                        onClick = { onClickAddEditAddress.invoke() },
                        label = stringResource(R.string.add)
                    )
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(7f)) {
                        Text(
                            text = stringResource(id = R.string.delivering_to),
                            color = AppTheme.colors.lightTextColor,
                            style = Typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = deliveryAddress,
                            style = Typography.bodyLarge
                        )
                    }
                    Row(
                        modifier = Modifier
                            .weight(3f)
                            .fillMaxWidth(), horizontalArrangement = Arrangement.End
                    ) {
                        AccentTextButton(onClick = { onClickAddEditAddress.invoke() }, label = stringResource(R.string.edit))
                    }
                }
            }
        }
    }
}

@Composable
fun BagItemRow(
    bagItemRowDisplayModel: BagItemRowDisplayModel,
    isRemoving: Boolean,
    onCheckChanged: (checked: Boolean, lineItemId: String) -> Unit
) {

    Row(modifier = Modifier.fillMaxWidth()) {

        Text(
            text = bagItemRowDisplayModel.qty,
            modifier = Modifier.weight(1f),
            style = Typography.bodyLarge
        )

        Column(
            modifier = Modifier
                .weight(12f)
                .padding(start = 8.dp)
        ) {
            Text(
                text = bagItemRowDisplayModel.itemName,
                style = Typography.bodyLarge
            )
            if (bagItemRowDisplayModel.itemModifier.isNotBlank()) {
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = bagItemRowDisplayModel.itemModifier,
                    color = AppTheme.colors.lightTextColor,
                    style = Typography.bodyLarge
                )
            }
        }

        Row(
            modifier = Modifier
                .weight(4f)
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isRemoving) {
                Checkbox(
                    onCheckedChange = { checked ->
                        onCheckChanged.invoke(checked, bagItemRowDisplayModel.lineItemId)
                    },
                    checked = bagItemRowDisplayModel.forRemoval,
                    colors = CheckboxDefaults.colors(checkedColor = CoralRed, checkmarkColor = Color.White)
                )
            } else {
                Text(
                    text = bagItemRowDisplayModel.price,
                    style = Typography.bodyLarge,
                    textAlign = TextAlign.End
                )

            }
        }


    }

}