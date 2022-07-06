package com.ryanjames.composemobileordering.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.ui.theme.AppTheme
import com.ryanjames.composemobileordering.ui.theme.TypeScaleCategory
import com.ryanjames.composemobileordering.ui.theme.TypeScaledTextView

@Composable
fun StoreInfoBottomSheetLayout(storeInfoDisplayModel: StoreInfoDisplayModel?) {
    if (storeInfoDisplayModel == null) return
    Column(modifier = Modifier.padding(horizontal = 32.dp)) {
        TypeScaledTextView(label = "Hours", typeScale = TypeScaleCategory.Subtitle2, overrideFontWeight = FontWeight.Bold)

        storeInfoDisplayModel.address?.let {
            StoreInfoSection(stringResource(R.string.address), storeInfoDisplayModel.address)
        }

        storeInfoDisplayModel.phone?.let {
            StoreInfoSection(stringResource(R.string.phone), storeInfoDisplayModel.phone)
        }
    }
}

@Composable
fun StoreInfoSection(heading: String, text: String) {
    TypeScaledTextView(label = heading, typeScale = TypeScaleCategory.Subtitle2, overrideFontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.size(8.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(4.dp))
                .background(AppTheme.colors.textBackground, RoundedCornerShape(4.dp))
        )
        TypeScaledTextView(
            label = text,
            typeScale = TypeScaleCategory.Subtitle1,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
        )
    }
    Spacer(modifier = Modifier.size(16.dp))
}

data class StoreInfoDisplayModel(
    val address: String?,
    val phone: String?
)