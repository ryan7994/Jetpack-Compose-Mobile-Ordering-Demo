package com.ryanjames.composemobileordering.features.venuedetail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.R.drawable
import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.ui.theme.*
import com.ryanjames.composemobileordering.ui.widget.*

@Composable
fun VenueDetailScreen(
    venueDetailViewModel: VenueDetailViewModel = hiltViewModel(),
    onClickMenuItemCard: (productId: String, venueId: String) -> Unit = { _, _ -> },
    onClickUpBtn: () -> Unit
) {
    val state = venueDetailViewModel.venueDetailScreenState.collectAsState()
    VenueDetailScreen(
        venueDetailScreenState = state.value,
        onClickMenuItemCard = onClickMenuItemCard,
        onClickUp = onClickUpBtn
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VenueDetailScreen(
    venueDetailScreenState: VenueDetailScreenState,
    onClickUp: () -> Unit = {},
    onClickMenuItemCard: (productId: String, venueId: String) -> Unit
) {

    Box {
        Column {
            venueDetailScreenState.header?.let {
                RestaurantHeader(state = venueDetailScreenState.header, onClickUp)
            }

            val listState = rememberLazyListState()

            when (venueDetailScreenState.menuCategoriesResource) {
                is Resource.Loading -> {
                    LoadingSpinnerWithText(text = stringResource(R.string.loading_menu))
                }
                is Resource.Success -> {
                    val menuCategories = venueDetailScreenState.menuCategoriesResource.data

                    if (menuCategories.isNotEmpty()) {
                        TextTabs(
                            tabs = menuCategories.map { it.categoryName },
                            selectedIndex = listState.firstVisibleItemIndex,
                            listState = listState,
                            selectedContent = { tabText ->
                                TypeScaledTextView(label = tabText, typeScale = TypeScaleCategory.Subtitle1, overrideFontWeight = FontWeight.Bold, color = TextColor.StaticColor(CoralRed))
                            },
                            unselectedContent = { tabText ->
                                TypeScaledTextView(label = tabText, typeScale = TypeScaleCategory.Subtitle1, overrideFontWeight = FontWeight.Bold, color = TextColor.StaticColor(HintGray))
                            }
                        )

                        LazyColumn(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            state = listState
                        ) {
                            items(count = menuCategories.size) { index ->
                                if (index == 0) {
                                    Spacer(modifier = Modifier.size(16.dp))
                                }
                                val category = menuCategories[index]
                                CategoryItems(category = category.categoryName, items = category.menuItems, onClickMenuItemCard = onClickMenuItemCard, venueId = venueDetailScreenState.venueId)

                                if (index == menuCategories.size - 1) {
                                    Spacer(modifier = Modifier.size(64.dp))
                                }
                            }
                        }
                    } else {
                        NoMenuView(
                            phoneUri = venueDetailScreenState.phoneUri,
                            email = venueDetailScreenState.email,
                            addressUri = venueDetailScreenState.addressUri
                        )
                    }
                }
                is Resource.Error -> TODO()
            }

        }
    }
}


@Composable
private fun NoMenuView(phoneUri: Uri?, email: String?, addressUri: Uri?) {

    Column {

        Card(
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppTheme.colors.materialColors.surface)
                    .padding(vertical = 16.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TypeScaledTextView(label = stringResource(R.string.come_back_later), typeScale = TypeScaleCategory.H7)
                TypeScaledTextView(label = stringResource(R.string.no_menu_message), typeScale = TypeScaleCategory.Subtitle1)
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {

            val context = LocalContext.current

            phoneUri?.let {
                CircularInfoButton(label = "Call", resId = drawable.phone) {
                    val callIntent = Intent(Intent.ACTION_DIAL, phoneUri)
                    startActivity(context, callIntent, null)
                }
            }

            email?.let {
                CircularInfoButton(label = "Email", resId = drawable.email) {
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                        data = Uri.parse("mailto:")
                    }
                    startActivity(context, Intent.createChooser(emailIntent, "Send Email Using: "), null)
                }
            }

            addressUri?.let {
                CircularInfoButton(label = "Directions", resId = drawable.direction) {
                    val mapIntent = Intent(Intent.ACTION_VIEW, addressUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(context, mapIntent, null)
                }
            }

        }
    }
}


@Composable
private fun CategoryItems(category: String, items: List<MenuItemCardDisplayModel>, onClickMenuItemCard: (productId: String, venueId: String) -> Unit, venueId: String) {
    TypeScaledTextView(label = category, typeScale = TypeScaleCategory.H6)
    Spacer(modifier = Modifier.size(16.dp))
    items.forEachIndexed { index, menuItemCardState ->
        MenuItemCard(state = menuItemCardState, onClickMenuItemCard = { onClickMenuItemCard.invoke(menuItemCardState.id, venueId) })
        Spacer(modifier = Modifier.size(if (index == items.size - 1) 16.dp else 12.dp))
    }

}

@Composable
fun VenueDetailScreen2() {
    Column(Modifier.verticalScroll(rememberScrollState())) {

        Box {

            Image(
                painter = painterResource(id = drawable.placeholder), contentDescription = "",
                modifier = Modifier
                    .fillMaxSize()
                    .height(250.dp),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )

        }


    }
}