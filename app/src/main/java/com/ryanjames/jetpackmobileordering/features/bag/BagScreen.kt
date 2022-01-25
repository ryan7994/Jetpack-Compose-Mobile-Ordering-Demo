package com.ryanjames.jetpackmobileordering.features.bag

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ryanjames.jetpackmobileordering.ui.theme.*

@Composable
fun BagScreen(bagViewModel: BagViewModel, onClickAddMoreItems: (venueId: String) -> Unit) {
    BagLayout(bagScreenState = bagViewModel.bagScreenState.value, onClickAddMoreItems)
}

@Composable
fun BagLayout(bagScreenState: BagScreenState, onClickAddMoreItems: (venueId: String) -> Unit) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TypeScaledTextView(label = "Your bag from:", typeScale = TypeScaleCategory.Subtitle2)
        TypeScaledTextView(label = "Jollibee", typeScale = TypeScaleCategory.H6)
        OutlinedAccentButton(onClick = {
            onClickAddMoreItems.invoke(bagScreenState.venueId ?: "")
        }, label = "+ Add more items")
        Spacer(modifier = Modifier.size(8.dp))
        BagSummaryCard(bagScreenState.bagItems)
    }
}

@Composable
fun BagSummaryCard(bagItems: List<BagItemRowState>) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
    ) {

        Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
            TypeScaledTextView(label = "Select item to modify", typeScale = TypeScaleCategory.Subtitle1, overrideFontWeight = FontWeight.Bold)

            bagItems.forEachIndexed { index, bagItemRowState ->
                Spacer(modifier = Modifier.size(12.dp))
                BagItemRow(bagItemRowState = bagItemRowState)
                Spacer(modifier = Modifier.size(12.dp))
                if (index < bagItems.size - 1) {
                    HorizontalLine()
                }
            }
        }

    }
}

@Composable
fun BagItemRow(bagItemRowState: BagItemRowState) {

    Row(modifier = Modifier.fillMaxWidth()) {

        TypeScaledTextView(
            label = bagItemRowState.qty, modifier = Modifier.weight(1f),
            typeScale = TypeScaleCategory.Subtitle1
        )

        Column(
            modifier = Modifier
                .weight(12f)
                .padding(start = 8.dp)
        ) {
            TypeScaledTextView(label = bagItemRowState.itemName, typeScale = TypeScaleCategory.Subtitle1)
            if (bagItemRowState.itemModifier.isNotBlank()) {
                TypeScaledTextView(label = bagItemRowState.itemModifier, color = TextColor.LightTextColor, typeScale = TypeScaleCategory.Subtitle2)
            }
        }

        TypeScaledTextView(label = bagItemRowState.price, modifier = Modifier.weight(4f), typeScale = TypeScaleCategory.Subtitle1, textAlign = TextAlign.End)
    }

}

data class BagScreenState(
    val bagItems: List<BagItemRowState>,
    val venueId: String?
)

data class BagItemRowState(
    val qty: String,
    val itemName: String,
    val itemModifier: String,
    val price: String
)