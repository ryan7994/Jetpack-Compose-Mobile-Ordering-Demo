@file:OptIn(ExperimentalMaterial3Api::class)

package com.ryanjames.composemobileordering.features.productdetail

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.core.StringResource
import com.ryanjames.composemobileordering.features.bottomnav.LocalCoroutineScope
import com.ryanjames.composemobileordering.features.bottomnav.LocalSnackbarHostState
import com.ryanjames.composemobileordering.ui.core.Dialog
import com.ryanjames.composemobileordering.ui.core.HorizontalLine
import com.ryanjames.composemobileordering.ui.theme.*
import com.ryanjames.composemobileordering.ui.widget.LoadingSpinnerWithText
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel,
    onSuccessfulAddOrUpdate: () -> Unit,
    onLoadFail: () -> Unit
) {

    val productDetalScreenState = viewModel.productDetailScreenState.collectAsState().value
    val snackbarMessage = stringResource(productDetalScreenState.addOrUpdateSuccessMessage.id)

    val snackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(Unit) {
        viewModel.onSuccessfulAddOrUpdate.collect { event ->
            event.handleSuspending { isSuccessful ->
                if (isSuccessful) {
                    onSuccessfulAddOrUpdate.invoke()
                    snackbarHostState.showSnackbar(snackbarMessage)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onLoadingFail.collect { event ->
            event.handleSuspending { failed ->
                if (failed) {
                    onLoadFail.invoke()
                }
            }
        }
    }

    ProductDetailLayout(
        productDetailScreenState = productDetalScreenState,
        onClickModifierSummary = viewModel::onClickModifierSummary,
        onClickModifier = viewModel::onClickModifierOption,
        onClickPlusQty = viewModel::onClickPlusQty,
        onClickMinusQty = viewModel::onClickMinusQty,
        onClickAddToBag = viewModel::onClickAddToBag
    )
}

@ExperimentalMaterialApi
@Composable
fun ProductDetailLayout(
    productDetailScreenState: ProductDetailScreenState,
    onClickModifierSummary: (String) -> Unit,
    onClickModifier: (parentId: String, id: String) -> Unit,
    onClickPlusQty: () -> Unit,
    onClickMinusQty: () -> Unit,
    onClickAddToBag: () -> Unit
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
            ModifierBottomSheetLayout(
                title = productDetailScreenState.modifierModalTitle,
                subtitle = productDetailScreenState.modifierModalSubtitle,
                modifierOptions = productDetailScreenState.modifierOptions,
                onClickModifier = onClickModifier
            )
        },
        sheetState = modalBottomSheetState,
        scrimColor = Color.Transparent,
        sheetBackgroundColor = AppTheme.colors.bottomNavBackground,
        sheetShape = RoundedCornerShape(topEnd = 32.dp, topStart = 32.dp),
        sheetElevation = 8.dp,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (productDetailScreenState.loadingProductDetail) {
                LoadingSpinnerWithText(text = stringResource(id = R.string.loading_product_details))
            } else {
                ProductDetailLayout(productDetailScreenState = productDetailScreenState) {
                    scope.launch {
                        onClickModifierSummary.invoke(it)
                        modalBottomSheetState.animateTo(ModalBottomSheetValue.Expanded)
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                    ) {
                        QtySelector(
                            modifier = Modifier.weight(1f),
                            onClickPlus = onClickPlusQty,
                            onClickMinus = onClickMinusQty,
                            qty = productDetailScreenState.quantity
                        )

                        Spacer(modifier = Modifier.size(8.dp))

                        AddToBagBtn(
                            modifier = Modifier.weight(2f),
                            price = productDetailScreenState.price,
                            onClickAddToBag = onClickAddToBag,
                            btnLabel = productDetailScreenState.btnLabel
                        )

                    }
                }
            }

            Dialog(productDetailScreenState.dialogState)
        }

    }
}

@Composable
private fun QtySelector(modifier: Modifier, onClickPlus: () -> Unit, onClickMinus: () -> Unit, qty: String) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(ChipGray)
            .clickable { },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(modifier = Modifier
            .fillMaxSize()
            .clickable { onClickMinus.invoke() }
            .weight(2f),
            contentAlignment = Alignment.Center) {
            Text(
                text = "-",
                style = Typography.titleLarge,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = qty,
            fontWeight = FontWeight.Bold,
            style = Typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            textAlign = TextAlign.Center,
            color = Color.White
        )

        Box(modifier = Modifier
            .fillMaxSize()
            .clickable { onClickPlus.invoke() }
            .weight(2f),
            contentAlignment = Alignment.Center) {
            Text(
                text = "+",
                style = Typography.titleLarge,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }


    }

}

@Composable
private fun AddToBagBtn(modifier: Modifier, price: String, onClickAddToBag: () -> Unit, btnLabel: StringResource) {
    Button(
        modifier = modifier.fillMaxSize(),
        onClick = { onClickAddToBag.invoke() },
        contentPadding = PaddingValues(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CoralRed),
        shape = RoundedCornerShape(8.dp),
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(btnLabel.id),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = Typography.titleMedium
            )
            Text(
                text = price,
                color = Color.White,
                style = Typography.titleMedium
            )
        }


    }
}

@Composable
private fun ProductDetailLayout(productDetailScreenState: ProductDetailScreenState, onClickModifier: (String) -> Unit) {
    val product = productDetailScreenState.product ?: return
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        product.imageUrl?.let {
            Spacer(modifier = Modifier.size(24.dp))
            GlideImage(
                imageModel = product.imageUrl,
                modifier = Modifier
                    .height(270.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(AppTheme.colors.placeholderColor),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
        }

        Spacer(modifier = Modifier.size(24.dp))

        Text(
            text = product.productName,
            style = Typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = product.productDescription,
            style = Typography.bodyLarge,
            color = AppTheme.colors.lightTextColor,
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.size(8.dp))

        productDetailScreenState.modifierSummaryRows.forEach { state ->
            if (state.isProductGroupHeader) {
                ProductGroupHeaderRow(label = state.title)
            } else {
                ModifierSummaryRow(state, onClickModifier)
            }
        }

        Spacer(modifier = Modifier.size(100.dp))
    }
}

@Composable
fun ModifierSummaryRow(modifierSummaryRowDisplayModel: ModifierSummaryRowDisplayModel, onClickModifier: (id: String) -> Unit) {

    Column(modifier = Modifier.clickable {
        onClickModifier.invoke(modifierSummaryRowDisplayModel.id)
    }) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp), verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(11f)) {
                Text(
                    text = modifierSummaryRowDisplayModel.title,
                    style = Typography.bodyLarge,
                    color = AppTheme.colors.darkTextColor
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = modifierSummaryRowDisplayModel.subtitle,
                    style = Typography.bodyMedium,
                    color = AppTheme.colors.lightTextColor
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                Image(
                    painter = painterResource(id = R.drawable.ic_keyboard_arrow_right), contentDescription = "",
                    modifier = Modifier.rotate(90f),
                    colorFilter = ColorFilter.tint(AppTheme.colors.darkTextColor)
                )
            }

        }

        if (!modifierSummaryRowDisplayModel.hideLineSeparator) {
            HorizontalLine()
        }
    }

}

@Composable
fun ProductGroupHeaderRow(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppTheme.colors.materialColors.surface)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center

    ) {
        Text(
            text = label,
            color = AppTheme.colors.darkTextColor,
            style = Typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }

}

@Composable
fun ModifierRbRow(modifierOptionDisplayModel: ModifierOptionDisplayModel, onClickModifier: (parentId: String, id: String) -> Unit) {
    Row(
        modifier = Modifier
            .clickable {
                onClickModifier.invoke(modifierOptionDisplayModel.parentId, modifierOptionDisplayModel.id)
            }
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = modifierOptionDisplayModel.name,
            modifier = Modifier.padding(start = 16.dp),
            style = Typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            color = AppTheme.colors.darkTextColor
        )

        RadioButton(
            selected = modifierOptionDisplayModel.selected,
            modifier = Modifier.padding(end = 16.dp),
            onClick = {
                onClickModifier.invoke(modifierOptionDisplayModel.parentId, modifierOptionDisplayModel.id)
            },
            enabled = true,
            colors = RadioButtonDefaults.colors(selectedColor = CoralRed)
        )
    }
}

@Composable
fun ModifierCbRow(modifierOptionDisplayModel: ModifierOptionDisplayModel, onClickModifier: (parentId: String, id: String) -> Unit) {
    Row(
        modifier = Modifier
            .clickable {
                if (modifierOptionDisplayModel.enabled) {
                    onClickModifier.invoke(modifierOptionDisplayModel.parentId, modifierOptionDisplayModel.id)
                }
            }
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = modifierOptionDisplayModel.name,
            modifier = Modifier.padding(start = 16.dp),
            style = Typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            color = AppTheme.colors.darkTextColor
        )
        Checkbox(
            checked = modifierOptionDisplayModel.selected,
            modifier = Modifier.padding(end = 16.dp),
            onCheckedChange = {
                onClickModifier.invoke(modifierOptionDisplayModel.parentId, modifierOptionDisplayModel.id)
            },
            enabled = modifierOptionDisplayModel.enabled,
            colors = CheckboxDefaults.colors(checkedColor = CoralRed, checkmarkColor = Color.White)
        )
    }
}


@ExperimentalMaterialApi
@Composable
fun ModifierBottomSheetLayout(
    title: String,
    subtitle: String,
    modifierOptions: List<ModifierOptionDisplayModel>,
    onClickModifier: (parentId: String, id: String) -> Unit
) {
    Column {
        Text(
            text = title,
            style = Typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
            color = AppTheme.colors.darkTextColor
        )

        if (subtitle.isNotBlank()) {
            Spacer(Modifier.size(4.dp))
            Text(
                text = subtitle,
                style = Typography.bodyLarge,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                color = AppTheme.colors.lightTextColor
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        modifierOptions.forEach {
            if (it.selectionType == ModifierOptionType.Single) {
                ModifierRbRow(it, onClickModifier)
            } else if (it.selectionType == ModifierOptionType.Multi) {
                ModifierCbRow(it, onClickModifier)
            }
        }

    }
}

@Preview
@Composable
fun PreviewModifierRow() {
    ModifierSummaryRow(ModifierSummaryRowDisplayModel("", "Title", "Subtitle", false), {})
}