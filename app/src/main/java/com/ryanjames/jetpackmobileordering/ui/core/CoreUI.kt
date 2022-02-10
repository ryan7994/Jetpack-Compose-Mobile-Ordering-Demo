package com.ryanjames.jetpackmobileordering.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryanjames.jetpackmobileordering.features.bag.ButtonState
import kotlinx.coroutines.launch

@Composable
fun TypeScaledTextView(
    label: String,
    modifier: Modifier = Modifier,
    color: TextColor = TextColor.DarkTextColor,
    maxLines: Int = Int.MAX_VALUE,
    typeScale: TypeScaleCategory = TypeScaleCategory.Subtitle2,
    overrideFontWeight: FontWeight? = null,
    textAlign: TextAlign? = null
) {
    val textColor = when (color) {
        is TextColor.DarkTextColor -> AppTheme.colors.darkTextColor
        is TextColor.LightTextColor -> AppTheme.colors.lightTextColor
        is TextColor.StaticColor -> color.color
        else -> Color.Black
    }

    Text(
        text = label,
        fontSize = typeScale.size,
        fontWeight = overrideFontWeight ?: typeScale.weight,
        color = textColor,
        fontFamily = FreeSans,
        modifier = modifier,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        textAlign = textAlign
    )
}

sealed class TextColor {
    object DarkTextColor : TextColor()
    object LightTextColor : TextColor()
    class StaticColor(val color: Color) : TextColor()
}

sealed class TypeScaleCategory(val size: TextUnit, val weight: FontWeight) {
    object H1 : TypeScaleCategory(42.sp, FontWeight.Bold)
    object H2 : TypeScaleCategory(38.sp, FontWeight.Bold)
    object H3 : TypeScaleCategory(34.sp, FontWeight.Bold)
    object H4 : TypeScaleCategory(28.sp, FontWeight.Bold)
    object H5 : TypeScaleCategory(24.sp, FontWeight.Bold)
    object H6 : TypeScaleCategory(20.sp, FontWeight.Bold)
    object H7 : TypeScaleCategory(18.sp, FontWeight.Bold)
    object Subtitle1 : TypeScaleCategory(16.sp, FontWeight.W500)
    object Subtitle2 : TypeScaleCategory(14.sp, FontWeight.W500)
    object Subtitle3 : TypeScaleCategory(12.sp, FontWeight.W500)
}


@Composable
fun SingleLineTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    hintText: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val focusManager = LocalFocusManager.current
        OutlinedTextField(
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = CoralRed,
                unfocusedBorderColor = AppTheme.colors.darkTextColor,
                cursorColor = CoralRed
            ),
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = modifier
                .fillMaxWidth(),
            placeholder = {
                Text(
                    text = hintText,
                    color = AppTheme.colors.hintTextColor,
                    fontFamily = FreeSans
                )
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions ?: KeyboardActions(onAny = { focusManager.clearFocus() }),
            visualTransformation = visualTransformation,
            textStyle = TextStyle(color = AppTheme.colors.darkTextColor)
        )


}

@Composable
fun AccentButton(onClick: () -> Unit, label: String, modifier: Modifier = Modifier) {

    Button(
        onClick = onClick,
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(backgroundColor = CoralRed),
        shape = RoundedCornerShape(8.dp)
    ) {

        Box(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = label,
                fontFamily = FreeSans,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = modifier
            )
        }

    }
}

@Composable
fun OutlinedAccentButton(onClick: () -> Unit, label: String, modifier: Modifier = Modifier) {

    OutlinedButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 8.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(width = 2.dp, color = CoralRed),
        colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Color.Transparent)
    ) {

        TypeScaledTextView(
            label = label,
            color = TextColor.DarkTextColor,
            textAlign = TextAlign.Center,
            modifier = modifier
        )
    }
}

@Composable
fun AccentTextButton(onClick: () -> Unit, label: String, modifier: Modifier = Modifier, buttonState: ButtonState? = ButtonState(true, true)) {

    if (buttonState?.visible == true) {
        TextButton(
            onClick = onClick,
            colors = ButtonDefaults.textButtonColors(),
            enabled = buttonState.enabled
        ) {

            TypeScaledTextView(
                label = label,
                color = if (buttonState.enabled) TextColor.StaticColor(CoralRed) else TextColor.LightTextColor,
                textAlign = TextAlign.Center,
                modifier = modifier,
                overrideFontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun FullWidthButton(onClick: () -> Unit, label: String) {
    val gradient = Brush.horizontalGradient(listOf(CoralRed, CoralRedGradientEnd))

    Button(
        onClick = onClick,
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(vertical = 12.dp)
                .fillMaxWidth()
        ) {

            Text(
                text = label,
                fontFamily = FreeSans,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

    }
}

@Composable
fun TextTabs(
    tabs: List<String>,
    selectedIndex: Int,
    listState: LazyListState,
    selectedContent: @Composable (text: String) -> Unit,
    unselectedContent: @Composable (text: String) -> Unit,
) {
    val tabIndex = remember { mutableStateOf(selectedIndex) }
    val coroutineScope = rememberCoroutineScope()
    val tabClicked = remember { mutableStateOf(false) }

    if (listState.isScrollInProgress && !tabClicked.value) {
        tabIndex.value = listState.firstVisibleItemIndex
    }

    ScrollableTabRow(
        selectedTabIndex = tabIndex.value,
        backgroundColor = AppTheme.colors.bottomNavBackground,
        edgePadding = 16.dp,
        indicator = {
            TabRowDefaults.Indicator(
                height = 4.dp,
                color = CoralRed,
                modifier = Modifier
                    .tabIndicatorOffset(it[tabIndex.value])
                    .clip(RoundedCornerShape(2.dp))
            )
        }
    ) {
        tabs.forEachIndexed { index, text ->
            val isSelected = tabIndex.value == index
            Tab(selected = isSelected,
                onClick = {
                    tabIndex.value = index
                    coroutineScope.launch {
                        tabClicked.value = true
                        listState.animateScrollToItem(index)
                        tabClicked.value = false
                    }
                },
                text = {
                    if (isSelected) {
                        selectedContent.invoke(text)
                    } else {
                        unselectedContent.invoke(text)
                    }

                })
        }
    }
}

@Composable
fun HorizontalLine() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(AppTheme.colors.placeholderColor)
    )
}

@Composable
fun DashedHorizontalLine() {
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)
    Canvas(
        Modifier.fillMaxWidth()

    ) {
        drawLine(
            strokeWidth = 8f,
            color = Color.LightGray,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect
        )
    }
}

val customTextSelectionColors = TextSelectionColors(
    handleColor = CoralRed,
    backgroundColor = CoralRed.copy(alpha = 0.4f)
)