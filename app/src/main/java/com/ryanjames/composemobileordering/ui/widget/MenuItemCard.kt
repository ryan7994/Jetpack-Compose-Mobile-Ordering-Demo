package com.ryanjames.composemobileordering.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.request.RequestOptions
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.ui.theme.*
import com.skydoves.landscapist.glide.GlideImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemCard(
    state: MenuItemCardDisplayModel,
    onClickMenuItemCard: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClickMenuItemCard.invoke(state.id) }
    ) {
        Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)) {

            GlideImage(
                imageModel = state.imageUrl,
                requestOptions = RequestOptions().circleCrop(),
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                placeHolder = painterResource(id = R.drawable.default_food_icon),
                error = painterResource(id = R.drawable.default_food_icon),
            )

            Spacer(modifier = Modifier.size(16.dp))


            Column {
                Text(
                    text = state.name,
                    style = Typography.bodyLarge
                )

                Spacer(modifier = Modifier.size(2.dp))

                Text(
                    text = state.calories,
                    style = Typography.bodyMedium,
                    color = AppTheme.colors.lightTextColor
                )

                Spacer(modifier = Modifier.size(2.dp))

                DisplayChip(
                    label = state.price,
                    textColor = Color.White,
                    backgroundColor = CoralRed
                )
            }
        }


    }

}

data class MenuItemCardDisplayModel(
    val id: String,
    val name: String,
    val calories: String,
    val price: String,
    val imageUrl: String
)