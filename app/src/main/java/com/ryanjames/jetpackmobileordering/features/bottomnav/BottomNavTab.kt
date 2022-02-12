package com.ryanjames.jetpackmobileordering.features.bottomnav

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.navArgument
import com.ryanjames.jetpackmobileordering.R

private const val TAB_ROUTE_BROWSE = "tabBrowse"
private const val TAB_ROUTE_BAG = "tabBag"
private const val TAB_ROUTE_MAP = "tabMap"
private const val SCREEN_ROUTE_VENUE_DETAIL = "venueDetail"
private const val SCREEN_ROUTE_VENUE_DETAIL_FROM_BAG = "venueDetailFromBag"
private const val SCREEN_ROUTE_PRODUCT_DETAIL = "productDetail"
private const val SCREEN_ROUTE_PRODUCT_DETAIL_FROM_BAG = "productDetailFromBag"
private const val SCREEN_ROUTE_HOME = "screenHome"
private const val SCREEN_ROUTE_BAG = "screenBag"
private const val SCREEN_ROUTE_MAP = "screenMap"

sealed class BottomNavTabs(open val tabRoute: String, val labelResId: Int = -1, val icon: ImageVector? = null, val drawableId: Int? = null) {
    object BrowseTab : BottomNavTabs(TAB_ROUTE_BROWSE, R.string.bottom_nav_browse, drawableId = R.drawable.search)
    object BagTab : BottomNavTabs(TAB_ROUTE_BAG, R.string.bottom_nav_bag, drawableId = R.drawable.shopping_bag_svgrepo_com)
    object MapTab : BottomNavTabs(TAB_ROUTE_MAP, R.string.map, drawableId = R.drawable.map)
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

    object Map: BottomNavScreens(SCREEN_ROUTE_MAP)


    object ProductDetailModal : BottomNavScreens("$SCREEN_ROUTE_PRODUCT_DETAIL/{productId}/{venueId}") {
        fun routeWithArgs(productId: String, venueId: String): String {
            return "$SCREEN_ROUTE_PRODUCT_DETAIL/$productId/$venueId"
        }
    }

    object ProductDetailFromBag : BottomNavScreens("$SCREEN_ROUTE_PRODUCT_DETAIL_FROM_BAG?productId={productId}&venueId={venueId}&lineItemId={lineItemId}") {
        fun routeWithArgs(productId: String, venueId: String): String {
            return "$SCREEN_ROUTE_PRODUCT_DETAIL_FROM_BAG?productId=$productId&venueId=$venueId"
        }

        fun routeWithArgs(lineItemId: String): String {
            return "$SCREEN_ROUTE_PRODUCT_DETAIL_FROM_BAG?lineItemId=$lineItemId"
        }

        fun navArguments(): List<NamedNavArgument> {
            return listOf(
                navArgument("productId") {
                    nullable = true
                    defaultValue = null
                    type = NavType.StringType
                }, navArgument("venueId") {
                    nullable = true
                    defaultValue = null
                    type = NavType.StringType
                }, navArgument("lineItemId") {
                    nullable = true
                    defaultValue = null
                    type = NavType.StringType
                })
        }
    }
}

