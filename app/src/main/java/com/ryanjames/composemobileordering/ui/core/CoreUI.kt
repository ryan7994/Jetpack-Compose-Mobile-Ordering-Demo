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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.features.bag.ButtonState
import com.ryanjames.composemobileordering.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SingleLineTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    hintText: String,
    keyboardActions: KeyboardActions? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Next,
        autoCorrect = false
    ),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false,
    onFocusChanged: (FocusState) -> Unit = {},
    errorMessage: String? = null
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = CoralRed,
            unfocusedBorderColor = AppTheme.colors.darkTextColor,
            cursorColor = CoralRed
        ),
        label = {
            Text(
                text = hintText,
                color = AppTheme.colors.hintTextColor,
                style = Typography.bodyLarge
            )
        },
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged(onFocusChanged),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions ?: KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            onDone = { focusManager.clearFocus() }
        ),
        visualTransformation = visualTransformation,
        textStyle = TextStyle(color = AppTheme.colors.darkTextColor, fontFamily = fontRubik),
        isError = isError
    )

    if (isError && errorMessage != null) {
        Spacer(modifier = Modifier.size(4.dp))
        Text(text = errorMessage, style = Typography.bodyMedium, color = Color.Red, modifier = Modifier.padding(start = 8.dp))
    }


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
    listState: LazyListState,
    selectedTabContent: @Composable (text: String) -> Unit,
    unselectedTabContent: @Composable (text: String) -> Unit,
) {
    val firstItemVisibleIndex by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex
        }
    }

    val selectedTabIndex = remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val tabClicked = remember { mutableStateOf(false) }

    if (listState.isScrollInProgress && !tabClicked.value && selectedTabIndex.value != firstItemVisibleIndex) {
        selectedTabIndex.value = firstItemVisibleIndex
    }

    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex.value,
        containerColor = AppTheme.colors.bottomNavBackground,
        edgePadding = 16.dp,
        indicator = {
            TabRowDefaults.Indicator(
                height = 4.dp,
                color = CoralRed,
                modifier = Modifier
                    .tabIndicatorOffset(it[selectedTabIndex.value])
                    .clip(RoundedCornerShape(2.dp))
            )
        }
    ) {
        tabs.forEachIndexed { index, text ->
            val isSelected = selectedTabIndex.value == index
            Tab(selected = isSelected,
                onClick = {
                    selectedTabIndex.value = index
                    coroutineScope.launch {
                        tabClicked.value = true
                        listState.animateScrollToItem(index)
                        tabClicked.value = false
                    }
                },
                text = {
                    if (isSelected) {
                        selectedTabContent.invoke(text)
                    } else {
                        unselectedTabContent.invoke(text)
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

@Composable
fun TopAppBarWithUpButton(title: String, onClickUpBtn: () -> Unit) {
    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            IconButton(onClick = onClickUpBtn) {
                Icon(Icons.Default.ArrowBack, stringResource(R.string.navigate_up), tint = AppTheme.colors.topBarTextColor)
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = AppTheme.colors.topBarColor,
            titleContentColor = AppTheme.colors.topBarTextColor
        )
    )
}

val customTextSelectionColors = TextSelectionColors(
    handleColor = CoralRed,
    backgroundColor = CoralRed.copy(alpha = 0.4f)
)