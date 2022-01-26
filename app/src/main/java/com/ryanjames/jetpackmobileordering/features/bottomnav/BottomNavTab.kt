package com.ryanjames.jetpackmobileordering.features.bottomnav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.ui.graphics.vector.ImageVector
import com.ryanjames.jetpackmobileordering.R

private const val TAB_ROUTE_BROWSE = "tabBrowse"
private const val TAB_ROUTE_BAG = "tabBag"
private const val SCREEN_ROUTE_VENUE_DETAIL = "venueDetail"
private const val SCREEN_ROUTE_VENUE_DETAIL_FROM_BAG = "venueDetailFromBag"
private const val SCREEN_ROUTE_PRODUCT_DETAIL = "productDetail"
private const val SCREEN_ROUTE_PRODUCT_DETAIL_FROM_BAG = "productDetailFromBag"
private const val SCREEN_ROUTE_HOME = "screenHome"
private const val SCREEN_ROUTE_BAG = "screenBag"

sealed class BottomNavTabs(open val tabRoute: String, val labelResId: Int = -1, val icon: ImageVector? = null) {
    object BrowseTab : BottomNavTabs(TAB_ROUTE_BROWSE, R.string.bottom_nav_browse, Icons.Default.ArrowBack)
    object BagTab : BottomNavTabs(TAB_ROUTE_BAG, R.string.bottom_nav_bag, Icons.Default.ThumbUp)
}

sealed class BottomNavScreens(open val route: String) {
    object Home : BottomNavScreens(SCREEN_ROUTE_HOME)
    object Bag : BottomNavScreens(SCREEN_ROUTE_BAG)

    object VenueDetail : BottomNavScreens("$SCREEN_ROUTE_VENUE_DETAIL/{venueId}") {
        fun routeWithArgs(venueId: String, rootTab: String? = null): String {
            return "$SCREEN_ROUTE_VENUE_DETAIL/$venueId"
        }
    }

    object VenueDetailFromBag : BottomNavScreens("$SCREEN_ROUTE_VENUE_DETAIL_FROM_BAG/{venueId}") {
        fun routeWithArgs(venueId: String): String {
            return "$SCREEN_ROUTE_VENUE_DETAIL_FROM_BAG/$venueId"
        }
    }


    object ProductDetailModal : BottomNavScreens("$SCREEN_ROUTE_PRODUCT_DETAIL/{productId}/{venueId}") {
        fun routeWithArgs(productId: String, venueId: String): String {
            return "$SCREEN_ROUTE_PRODUCT_DETAIL/$productId/$venueId"
        }
    }

    object ProductDetailFromBag : BottomNavScreens("$SCREEN_ROUTE_PRODUCT_DETAIL_FROM_BAG/{productId}/{venueId}") {
        fun routeWithArgs(productId: String, venueId: String): String {
            return "$SCREEN_ROUTE_PRODUCT_DETAIL_FROM_BAG/$productId/$venueId"
        }
    }
}

