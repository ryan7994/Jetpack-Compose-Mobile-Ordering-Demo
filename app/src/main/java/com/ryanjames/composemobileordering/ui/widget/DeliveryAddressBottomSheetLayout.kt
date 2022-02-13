package com.ryanjames.composemobileordering.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.ui.theme.FullWidthButton
import com.ryanjames.composemobileordering.ui.theme.SingleLineTextField
import com.ryanjames.composemobileordering.ui.theme.TypeScaleCategory
import com.ryanjames.composemobileordering.ui.theme.TypeScaledTextView

@Composable
fun DeliveryAddressBottomSheetLayout(onClickSave: () -> Unit, onValueChange: (String) -> Unit, value: String) {
    val focusManager = LocalFocusManager.current
    Column(modifier = Modifier.padding(top = 32.dp, bottom = 32.dp, start = 16.dp, end = 16.dp)) {
        TypeScaledTextView(label = stringResource(R.string.enter_deliver_address), typeScale = TypeScaleCategory.H6)
        Spacer(modifier = Modifier.size(16.dp))
        SingleLineTextField(
            value = value,
            onValueChange = { onValueChange.invoke(it) },
            hintText = stringResource(R.string.address)
        )
        Spacer(modifier = Modifier.size(16.dp))
        FullWidthButton(
            onClick = {
                onClickSave.invoke()
                focusManager.clearFocus()
            },
            label = stringResource(R.string.save)
        )
    }
}