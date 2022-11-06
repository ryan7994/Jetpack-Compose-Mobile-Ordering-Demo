@file:OptIn(ExperimentalMaterial3Api::class)

package com.ryanjames.composemobileordering.ui.core

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryanjames.composemobileordering.features.bag.ButtonState
import com.ryanjames.composemobileordering.ui.theme.*
import kotlinx.coroutines.launch


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
                style = Typography.bodyLarge
            )
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions ?: KeyboardActions(onAny = { focusManager.clearFocus() }),
        visualTransformation = visualTransformation,
        textStyle = TextStyle(color = AppTheme.colors.darkTextColor, fontFamily = fontRubik)
    )


}

@Composable
fun AccentButton(onClick: () -> Unit, label: String, modifier: Modifier = Modifier) {

    Button(
        onClick = onClick,
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(containerColor = CoralRed),
        shape = RoundedCornerShape(8.dp)
    ) {

        Box(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = modifier,
                style = Typography.titleLarge
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
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
    ) {

        Text(
            text = label,
            color = AppTheme.colors.darkTextColor,
            textAlign = TextAlign.Center,
            modifier = modifier,
            style = Typography.bodyLarge
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

            Text(
                text = label,
                color = if (buttonState.enabled) CoralRed else AppTheme.colors.lightTextColor,
                textAlign = TextAlign.Center,
                modifier = modifier,
                style = Typography.titleMedium
            )
        }
    }
}

@Composable
fun FullWidthButton(onClick: () -> Unit, label: String, tag: String? = null) {
    val gradient = Brush.horizontalGradient(listOf(CoralRed, CoralRedGradientEnd))

    Button(
        onClick = onClick,
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag(tag ?: label)
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(vertical = 12.dp)
                .fillMaxWidth()
        ) {

            Text(
                text = label,
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = Typography.titleLarge
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
        containerColor = AppTheme.colors.bottomNavBackground,
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