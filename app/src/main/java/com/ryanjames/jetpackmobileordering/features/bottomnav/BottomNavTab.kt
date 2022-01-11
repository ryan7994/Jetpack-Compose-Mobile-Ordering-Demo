package com.ryanjames.jetpackmobileordering.features.bottomnav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.ui.graphics.vector.ImageVector
import com.ryanjames.jetpackmobileordering.R

private const val ROUTE_BROWSE_TAB = "HomeTab"
private const val ROUTE_ORDER_TAB = "OrderTab"
private const val ROUTE_BAG_TAB = "BagTab"

private val VENUE_DETAIL_ROUTE = "venueDetail"
private val PRODUCT_DETAIL_ROUTE = "productDetail"

sealed class BottomNavScreen(open val route: String, val rootTab: String, val labelResId: Int = -1, val icon: ImageVector? = null) {
    object Home : BottomNavScreen("Home", ROUTE_BROWSE_TAB, R.string.bottom_nav_browse, Icons.Default.ArrowBack)
    object Login : BottomNavScreen("Login", ROUTE_ORDER_TAB, R.string.bottom_nav_bag, Icons.Default.ThumbUp)
    object Random : BottomNavScreen("Random", ROUTE_BAG_TAB, R.string.bottom_nav_order, Icons.Default.Lock)

    object VenueDetail : BottomNavScreen("$VENUE_DETAIL_ROUTE/{venueId}", ROUTE_BROWSE_TAB) {
        fun routeWithArgs(venueId: String): String {
            return "$VENUE_DETAIL_ROUTE/$venueId"
        }
    }

    object ProductDetailModal : BottomNavScreen("$PRODUCT_DETAIL_ROUTE/{productId}", ROUTE_BROWSE_TAB) {
        fun routeWithArgs(productId: String): String {
            return "$PRODUCT_DETAIL_ROUTE/$productId"
        }
    }

    object VenueDetail2 : BottomNavScreen("Venue Detail2", ROUTE_ORDER_TAB)

    companion object {
        fun getTabRoute(route: String): String {
            return BottomNavScreen::class.sealedSubclasses
                .firstOrNull { it.objectInstance?.route == route }
                ?.objectInstance
                ?.rootTab ?: ""
        }
    }
}

