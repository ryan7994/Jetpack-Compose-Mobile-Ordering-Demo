package com.ryanjames.jetpackmobileordering.features.bag

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.ryanjames.jetpackmobileordering.R
import com.ryanjames.jetpackmobileordering.features.bottomnav.LocalCoroutineScope
import com.ryanjames.jetpackmobileordering.features.bottomnav.LocalSnackbarHostState
import com.ryanjames.jetpackmobileordering.ui.core.Dialog
import com.ryanjames.jetpackmobileordering.ui.theme.*
import com.ryanjames.jetpackmobileordering.ui.widget.DeliveryAddressBottomSheetLayout
import com.ryanjames.jetpackmobileordering.util.getBitmapDescriptor
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@FlowPreview
@Composable
fun BagScreen(
    bagViewModel: BagViewModel,
    onClickAddMoreItems: (venueId: String) -> Unit,
    onClickLineItem: (lineItemId: String) -> Unit,
    onClickBrowseRestaurants: () -> Unit
) {
    BagLayout(
        bagScreenState = bagViewModel.bagScreenState.collectAsState().value,
        onClickAddMoreItems = onClickAddMoreItems,
        onClickLineItem = onClickLineItem,
        onClickRemove = bagViewModel::onClickRemove,
        onClickCancel = bagViewModel::onClickCancel,
        onClickRemoveSelected = bagViewModel::onClickRemoveSelected,
        onCheckChanged = bagViewModel::onRemoveCbCheckChanged,
        onClickPickup = bagViewModel::onClickPickup,
        onClickDelivery = bagViewModel::onClickDelivery,
        onDeliveryAddressValueChange = bagViewModel::onDeliveryAddressInputChange,
        onClickSaveDeliveryAddress = bagViewModel::updateDeliveryAddress,
        onClickBrowseRestaurants = onClickBrowseRestaurants
    )
    val globalScope = LocalCoroutineScope.current
    val snackbarHostState = LocalSnackbarHostState.current
    val snackbarMessage = stringResource(R.string.item_removed)

    LaunchedEffect(Unit) {
        globalScope.launch {
            bagViewModel.onItemRemoval.collect { event ->
                if (event.peekContent()) {
                    event.handleSuspendingEvent {
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
    onClickBrowseRestaurants: () -> Unit
) {

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    val scope = rememberCoroutineScope()

    BackHandler {
        if (modalBottomSheetState.isVisible) {
            scope.launch {
                modalBottomSheetState.hide()
            }
        }
    }


    ModalBottomSheetLayout(
        sheetContent = {
            DeliveryAddressBottomSheetLayout(value = bagScreenState.deliveryAddressInput, onValueChange = onDeliveryAddressValueChange, onClickSave = {
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
                        TypeScaledTextView(label = stringResource(R.string.your_bag_from), typeScale = TypeScaleCategory.Subtitle2)
                        TypeScaledTextView(label = bagScreenState.venueName, typeScale = TypeScaleCategory.H6)
                    }

                    OutlinedAccentButton(onClick = {
                        bagScreenState.venueId?.let { onClickAddMoreItems.invoke(it) }
                    }, label = stringResource(R.string.add_more_items))
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
                }
            }
            Dialog(bagScreenState.alertDialog)
        }
    }


}

@Composable
fun OrderPriceBreakdown(subtotal: String, tax: String, total: String) {
    Column {
        TypeScaledTextView(label = stringResource(R.string.summary), typeScale = TypeScaleCategory.H6)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TypeScaledTextView(label = stringResource(R.string.subtotal), typeScale = TypeScaleCategory.Subtitle1, color = TextColor.LightTextColor)
            TypeScaledTextView(label = subtotal, typeScale = TypeScaleCategory.Subtitle1, color = TextColor.DarkTextColor)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TypeScaledTextView(label = stringResource(R.string.tax), typeScale = TypeScaleCategory.Subtitle1, color = TextColor.LightTextColor)
            TypeScaledTextView(label = tax, typeScale = TypeScaleCategory.Subtitle1, color = TextColor.DarkTextColor)
        }
        Spacer(modifier = Modifier.size(8.dp))
        DashedHorizontalLine()
        Spacer(modifier = Modifier.size(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TypeScaledTextView(label = stringResource(R.string.total), typeScale = TypeScaleCategory.Subtitle1, color = TextColor.DarkTextColor, overrideFontWeight = FontWeight.Bold)
            TypeScaledTextView(label = total, typeScale = TypeScaleCategory.Subtitle1, color = TextColor.DarkTextColor, overrideFontWeight = FontWeight.Bold)
        }
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
        elevation = 4.dp,
    ) {

        Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
            TypeScaledTextView(label = stringResource(R.string.select_item_to_modify), typeScale = TypeScaleCategory.Subtitle1, overrideFontWeight = FontWeight.Bold)

            bagItems.forEachIndexed { index, bagItemRowState ->
                Column(modifier = Modifier.clickable {
                    onClickLineItem.invoke(bagItemRowState.lineItemId)
                }) {
                    Spacer(modifier = Modifier.size(12.dp))
                    BagItemRow(bagItemRowDisplayModel = bagItemRowState, isRemoving = isRemoving, onCheckChanged = onCheckChanged)
                    Spacer(modifier = Modifier.size(12.dp))
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
    val selectedColor = ButtonDefaults.outlinedButtonColors(backgroundColor = CoralRed)
    val unselectedColor = ButtonDefaults.outlinedButtonColors()
    val selectedTextColor = TextColor.StaticColor(Color.White)
    val unselectedTextColor = TextColor.DarkTextColor

    Row(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            colors = if (pickupSelected) selectedColor else unselectedColor,
            shape = RoundedCornerShape(topStart = radius, bottomStart = radius),
            onClick = { onClickPickup.invoke() },
            modifier = Modifier.weight(1f)
        ) {
            TypeScaledTextView(
                label = stringResource(R.string.pickup),
                color = if (pickupSelected) selectedTextColor else unselectedTextColor,
                typeScale = TypeScaleCategory.Subtitle1,
                overrideFontWeight = FontWeight.Bold
            )
        }
        OutlinedButton(
            colors = if (!pickupSelected) selectedColor else unselectedColor,
            shape = RoundedCornerShape(topEnd = radius, bottomEnd = radius),
            onClick = { onClickDelivery.invoke() },
            modifier = Modifier.weight(1f)
        ) {
            TypeScaledTextView(
                label = stringResource(R.string.delivery),
                color = if (!pickupSelected) selectedTextColor else unselectedTextColor,
                typeScale = TypeScaleCategory.Subtitle1,
                overrideFontWeight = FontWeight.Bold
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
                    position = latLng,
                    title = ".",
                    onClick = {
                        false
                    }
                )
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                TypeScaledTextView(
                    label = stringResource(R.string.pickup_from), color = TextColor.LightTextColor
                )
                TypeScaledTextView(
                    typeScale = TypeScaleCategory.Subtitle1, label = address
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
        TypeScaledTextView(label = "Your bag is empty", typeScale = TypeScaleCategory.Subtitle1)
        Spacer(modifier = Modifier.size(8.dp))
        OutlinedAccentButton(onClick = { onClickBrowseRestaurants.invoke() }, label = "Browse restaurants")
    }

}

@Composable
fun DeliveryAddressCard(deliveryAddress: String?, onClickAddEditAddress: () -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 16.dp)) {
            if (deliveryAddress == null) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    TypeScaledTextView(label = stringResource(R.string.no_address_provided), color = TextColor.LightTextColor)
                    AccentTextButton(onClick = { onClickAddEditAddress.invoke() }, label = stringResource(R.string.add))
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(7f)) {
                        TypeScaledTextView(label = stringResource(id = R.string.delivering_to), color = TextColor.LightTextColor)
                        TypeScaledTextView(label = deliveryAddress, color = TextColor.DarkTextColor)
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

        TypeScaledTextView(
            label = bagItemRowDisplayModel.qty, modifier = Modifier.weight(1f),
            typeScale = TypeScaleCategory.Subtitle1
        )

        Column(
            modifier = Modifier
                .weight(12f)
                .padding(start = 8.dp)
        ) {
            TypeScaledTextView(label = bagItemRowDisplayModel.itemName, typeScale = TypeScaleCategory.Subtitle1)
            if (bagItemRowDisplayModel.itemModifier.isNotBlank()) {
                TypeScaledTextView(label = bagItemRowDisplayModel.itemModifier, color = TextColor.LightTextColor, typeScale = TypeScaleCategory.Subtitle2)
            }
        }

        Row(
            modifier = Modifier
                .weight(4f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (isRemoving) {
                Checkbox(
                    modifier = Modifier,
                    onCheckedChange = { checked ->
                        onCheckChanged.invoke(checked, bagItemRowDisplayModel.lineItemId)
                    },
                    checked = bagItemRowDisplayModel.forRemoval
                )
            } else {
                TypeScaledTextView(label = bagItemRowDisplayModel.price, modifier = Modifier.weight(4f), typeScale = TypeScaleCategory.Subtitle1, textAlign = TextAlign.End)

            }
        }


    }

}