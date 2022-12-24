package com.ryanjames.composemobileordering.features.venuedetail

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.R.drawable
import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.ui.core.TextTabs
import com.ryanjames.composemobileordering.ui.theme.*
import com.ryanjames.composemobileordering.ui.widget.*
import kotlinx.coroutines.launch

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

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    val scope = rememberCoroutineScope()

    BackHandler(enabled = modalBottomSheetState.isVisible) {
        scope.launch {
            modalBottomSheetState.hide()
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            Spacer(modifier = Modifier.size(32.dp))
            StoreInfoBottomSheetLayout(venueDetailScreenState.storeInfoDisplayModel)

        },
        sheetState = modalBottomSheetState,
        scrimColor = Color.Transparent,
        sheetBackgroundColor = AppTheme.colors.bottomNavBackground,
        sheetShape = RoundedCornerShape(topEnd = 32.dp, topStart = 32.dp),
        sheetElevation = 8.dp
    ) {
        Box {
            Column {
                venueDetailScreenState.header?.let {
                    RestaurantHeader(state = venueDetailScreenState.header, onClickUp, onClickInfo = {
                        scope.launch {
                            modalBottomSheetState.show()
                        }
                    })
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
                                    Text(text = tabText, fontWeight = FontWeight.Bold, color = CoralRed, style = Typography.titleMedium)
                                },
                                unselectedContent = { tabText ->
                                    Text(text = tabText, fontWeight = FontWeight.Bold, color = HintGray, style = Typography.titleMedium)
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
                                    CategoryItems(
                                        category = category.categoryName,
                                        items = category.menuItems,
                                        onClickMenuItemCard = onClickMenuItemCard,
                                        venueId = venueDetailScreenState.venueId
                                    )

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
                    else -> {}
                }

            }
        }
    }


}


@Composable
private fun NoMenuView(phoneUri: Uri?, email: String?, addressUri: Uri?) {

    Column {

        androidx.compose.material3.Card(
            shape = RectangleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppTheme.colors.materialColors.surfaceVariant)
                    .padding(vertical = 16.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.come_back_later),
                    style = Typography.titleLarge
                )
                Text(
                    text = stringResource(R.string.no_menu_message),
                    style = Typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {

            val context = LocalContext.current

            phoneUri?.let {
                CircularInfoButton(label = stringResource(R.string.call), resId = drawable.phone) {
                    val callIntent = Intent(Intent.ACTION_DIAL, phoneUri)
                    startActivity(context, callIntent, null)
                }
            }

            email?.let {
                CircularInfoButton(label = stringResource(R.string.email), resId = drawable.email) {
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
    Text(text = category, style = Typography.titleMedium, color = AppTheme.colors.darkTextColor)
    Spacer(modifier = Modifier.size(16.dp))
    items.forEachIndexed { index, menuItemCardState ->
        MenuItemCard(state = menuItemCardState, onClickMenuItemCard = { onClickMenuItemCard.invoke(menuItemCardState.id, venueId) })
        Spacer(modifier = Modifier.size(if (index == items.size - 1) 16.dp else 12.dp))
    }

}