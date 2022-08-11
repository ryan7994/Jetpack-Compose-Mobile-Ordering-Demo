package com.ryanjames.composemobileordering.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.ui.theme.*

@Composable
fun DeliveryAddressBottomSheetLayout(onClickSave: () -> Unit, onValueChange: (String) -> Unit, value: String) {
    val focusManager = LocalFocusManager.current
    Column(modifier = Modifier.padding(top = 32.dp, bottom = 32.dp, start = 16.dp, end = 16.dp)) {
        Text(
            text = stringResource(R.string.enter_deliver_address),
            style = RubikTypography.titleLarge,
            color = AppTheme.colors.darkTextColor
        )
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