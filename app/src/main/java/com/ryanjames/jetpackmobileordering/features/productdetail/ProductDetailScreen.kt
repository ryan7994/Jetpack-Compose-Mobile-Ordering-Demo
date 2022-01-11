package com.ryanjames.jetpackmobileordering.features.productdetail

import android.widget.Space
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.material.chip.Chip
import com.ryanjames.jetpackmobileordering.R
import com.ryanjames.jetpackmobileordering.ui.theme.*
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel
) {
    val product = viewModel.productDetailScreenState.collectAsState().value
    ProductDetailScreen(
        productDetailScreenState = product,
        onClickModifierSummary = viewModel::onClickModifierSummary,
        onClickModifier = viewModel::onClickModifierOption,
        onClickPlusQty = viewModel::onClickPlusQty,
        onClickMinusQty = viewModel::onClickMinusQty
    )
}

@ExperimentalMaterialApi
@Composable
fun ProductDetailScreen(
    productDetailScreenState: ProductDetailScreenState,
    onClickModifierSummary: (String) -> Unit,
    onClickModifier: (parentId: String, id: String) -> Unit,
    onClickPlusQty: () -> Unit,
    onClickMinusQty: () -> Unit
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
                    QtySelector(modifier = Modifier.weight(1f), onClickPlus = onClickPlusQty, onClickMinus = onClickMinusQty, qty = productDetailScreenState.quantity)
                    Spacer(modifier = Modifier.size(8.dp))
                    AddToBagBtn(modifier = Modifier.weight(2f), price = productDetailScreenState.price)

                }
            }
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
            TypeScaledTextView(
                label = "-",
                overrideFontWeight = FontWeight.Bold,
                typeScale = TypeScaleCategory.H6,
                textAlign = TextAlign.Center,
                color = TextColor.StaticColor(Color.White)
            )
        }

        TypeScaledTextView(
            label = qty, overrideFontWeight = FontWeight.Bold,
            typeScale = TypeScaleCategory.H6,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            textAlign = TextAlign.Center,
            color = TextColor.StaticColor(Color.White)
        )

        Box(modifier = Modifier
            .fillMaxSize()
            .clickable { onClickPlus.invoke() }
            .weight(2f),
            contentAlignment = Alignment.Center) {
            TypeScaledTextView(
                label = "+",
                overrideFontWeight = FontWeight.Bold,
                typeScale = TypeScaleCategory.H6,
                textAlign = TextAlign.Center,
                color = TextColor.StaticColor(Color.White)
            )
        }


    }

}

@Composable
private fun AddToBagBtn(modifier: Modifier, price: String) {
    Button(
        modifier = modifier.fillMaxSize(),
        onClick = {},
        contentPadding = PaddingValues(16.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = CoralRed),
        shape = RoundedCornerShape(8.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TypeScaledTextView(
                label = "Add to bag",
                color = TextColor.StaticColor(Color.White),
                modifier = Modifier,
                typeScale = TypeScaleCategory.Subtitle1,
                overrideFontWeight = FontWeight.Bold
            )
            TypeScaledTextView(
                label = price,
                color = TextColor.StaticColor(Color.White),
                modifier = Modifier,
                typeScale = TypeScaleCategory.Subtitle1
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

        TypeScaledTextView(
            label = product.productName, typeScale = TypeScaleCategory.H5,
            color = TextColor.DarkTextColor
        )

        Spacer(modifier = Modifier.size(8.dp))

        TypeScaledTextView(
            label = product.productDescription,
            typeScale = TypeScaleCategory.Subtitle1,
            color = TextColor.LightTextColor
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
                TypeScaledTextView(label = modifierSummaryRowDisplayModel.title, typeScale = TypeScaleCategory.Subtitle1, overrideFontWeight = FontWeight.Bold)
                TypeScaledTextView(label = modifierSummaryRowDisplayModel.subtitle, typeScale = TypeScaleCategory.Subtitle2, color = TextColor.LightTextColor)
            }

            Box(modifier = Modifier.weight(1f)) {
                Image(
                    painter = painterResource(id = R.drawable.ic_keyboard_arrow_right), contentDescription = "",
                    modifier = Modifier.rotate(90f)
                )
            }

        }

        if (!modifierSummaryRowDisplayModel.hideLineSeparator) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(AppTheme.colors.placeholderColor)
            )
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
        TypeScaledTextView(
            label = label,
            typeScale = TypeScaleCategory.H6,
            color = TextColor.DarkTextColor
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
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TypeScaledTextView(label = modifierOptionDisplayModel.name, modifier = Modifier.padding(start = 16.dp), typeScale = TypeScaleCategory.Subtitle1)
        RadioButton(
            selected = modifierOptionDisplayModel.selected,
            modifier = Modifier.padding(end = 16.dp),
            onClick = {
                onClickModifier.invoke(modifierOptionDisplayModel.parentId, modifierOptionDisplayModel.id)
            },
            enabled = true
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
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TypeScaledTextView(label = modifierOptionDisplayModel.name, modifier = Modifier.padding(start = 16.dp), typeScale = TypeScaleCategory.Subtitle1)
        Checkbox(
            checked = modifierOptionDisplayModel.selected,
            modifier = Modifier.padding(end = 16.dp),
            onCheckedChange = {
                onClickModifier.invoke(modifierOptionDisplayModel.parentId, modifierOptionDisplayModel.id)
            },
            enabled = modifierOptionDisplayModel.enabled
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
        TypeScaledTextView(label = title, typeScale = TypeScaleCategory.H7, modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp))
        if (subtitle.isNotBlank()) {
            TypeScaledTextView(
                label = subtitle,
                typeScale = TypeScaleCategory.Subtitle2,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                color = TextColor.LightTextColor
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