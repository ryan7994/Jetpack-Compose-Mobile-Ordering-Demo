package com.ryanjames.jetpackmobileordering.features.bag

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ryanjames.jetpackmobileordering.R
import com.ryanjames.jetpackmobileordering.features.bottomnav.LocalCoroutineScope
import com.ryanjames.jetpackmobileordering.features.bottomnav.LocalSnackbarHostState
import com.ryanjames.jetpackmobileordering.ui.core.Dialog
import com.ryanjames.jetpackmobileordering.ui.theme.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun BagScreen(
    bagViewModel: BagViewModel,
    onClickAddMoreItems: (venueId: String) -> Unit,
    onClickLineItem: (lineItemId: String) -> Unit
) {
    BagLayout(
        bagScreenState = bagViewModel.bagScreenState.collectAsState().value,
        onClickAddMoreItems = onClickAddMoreItems,
        onClickLineItem = onClickLineItem,
        onClickRemove = bagViewModel::onClickRemove,
        onClickCancel = bagViewModel::onClickCancel,
        onClickRemoveSelected = bagViewModel::onClickRemoveSelected,
        onCheckChanged = bagViewModel::onRemoveCbCheckChanged
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


@Composable
fun BagLayout(
    bagScreenState: BagScreenState,
    onClickAddMoreItems: (venueId: String) -> Unit,
    onClickLineItem: (lineItemId: String) -> Unit,
    onClickRemove: () -> Unit,
    onClickCancel: () -> Unit,
    onClickRemoveSelected: () -> Unit,
    onCheckChanged: (checked: Boolean, lineItemId: String) -> Unit
) {
    Box {

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
            OrderPriceBreakdown(bagScreenState.subtotal, bagScreenState.tax, bagScreenState.total)
        }

        Dialog(bagScreenState.alertDialog)
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