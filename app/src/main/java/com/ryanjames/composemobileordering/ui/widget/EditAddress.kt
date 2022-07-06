package com.ryanjames.composemobileordering.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.ui.theme.DarkBlueGray
import com.ryanjames.composemobileordering.ui.theme.TextColor
import com.ryanjames.composemobileordering.ui.theme.TypeScaleCategory
import com.ryanjames.composemobileordering.ui.theme.TypeScaledTextView

@Composable
fun EditAddress(modifier: Modifier = Modifier, address: String?, onClick: () -> Unit) {
    Row(modifier = modifier.clickable {
        onClick.invoke()
    }) {
        Image(
            painter = painterResource(id = R.drawable.edit),
            contentDescription = stringResource(R.string.edit_address),
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.CenterVertically)
        )
        Column(modifier = Modifier.padding(8.dp)) {
            if (address != null) {
                TypeScaledTextView(label = stringResource(R.string.delivering_to), color = TextColor.StaticColor(Color.White), typeScale = TypeScaleCategory.H6)
                TypeScaledTextView(label = address, color = TextColor.StaticColor(Color.White), typeScale = TypeScaleCategory.Subtitle2)
            } else {
                TypeScaledTextView(label = stringResource(R.string.set_delivery_address), color = TextColor.StaticColor(Color.White), typeScale = TypeScaleCategory.H6)
            }
        }
    }
}




@Preview
@Composable
fun PreviewEditAddress() {
    Box(modifier = Modifier.background(DarkBlueGray)) {
        EditAddress(address = "Address", onClick = {})
    }
}
