package com.ryanjames.jetpackmobileordering.features.bottomnav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.ui.graphics.vector.ImageVector
import com.ryanjames.jetpackmobileordering.R

private const val ROUTE_BROWSE_TAB = "homeScreen"
private const val ROUTE_ORDER_TAB = "OrderTab"
private const val ROUTE_BAG_TAB = "bagScreen"
private const val VENUE_DETAIL_ROUTE = "venueDetail"
private const val VENUE_DETAIL2_ROUTE = "venueDetail2"
private const val PRODUCT_DETAIL_ROUTE = "productDetail"

sealed class BottomNavScreen(open val route: String, var rootTab: String, val labelResId: Int = -1, val icon: ImageVector? = null) {
    object Home : BottomNavScreen("Home", ROUTE_BROWSE_TAB, R.string.bottom_nav_browse, Icons.Default.ArrowBack)
    object Bag : BottomNavScreen("Bag", ROUTE_BAG_TAB, R.string.bottom_nav_bag, Icons.Default.ThumbUp)
    object Login : BottomNavScreen("Login", ROUTE_ORDER_TAB, R.string.bottom_nav_bag, Icons.Default.ThumbUp)

    object VenueDetail : BottomNavScreen("$VENUE_DETAIL_ROUTE/{venueId}", "Home") {
        fun routeWithArgs(venueId: String, rootTab: String? = null): String {
            return "$VENUE_DETAIL_ROUTE/$venueId"
        }
    }


    object ProductDetailModal : BottomNavScreen("$PRODUCT_DETAIL_ROUTE/{productId}/{venueId}", ROUTE_BROWSE_TAB) {
        fun routeWithArgs(productId: String, venueId: String): String {
            return "$PRODUCT_DETAIL_ROUTE/$productId/$venueId"
        }
    }

    companion object {
        fun getTabRoute(route: String): String {
            return BottomNavScreen::class.sealedSubclasses
                .firstOrNull { it.objectInstance?.route == route }
                ?.objectInstance
                ?.rootTab ?: ""
        }
    }
}

