@file:OptIn(FlowPreview::class, ExperimentalMaterialApi::class)

package com.ryanjames.composemobileordering.navigation

import android.content.Intent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDeepLink
import androidx.navigation.navDeepLink
import com.ryanjames.composemobileordering.features.bag.BagScreen
import com.ryanjames.composemobileordering.features.bag.BagViewModel
import com.ryanjames.composemobileordering.features.bottomnav.BottomNavScreens
import com.ryanjames.composemobileordering.features.home.HomeScreen
import com.ryanjames.composemobileordering.features.home.HomeViewModel
import com.ryanjames.composemobileordering.features.productdetail.ProductDetailScreen
import com.ryanjames.composemobileordering.features.productdetail.ProductDetailViewModel
import com.ryanjames.composemobileordering.features.venuedetail.VenueDetailScreen
import com.ryanjames.composemobileordering.features.venuedetail.VenueDetailViewModel
import com.ryanjames.composemobileordering.features.venuemapfinder.VenueFinderScreen
import com.ryanjames.composemobileordering.features.venuemapfinder.VenueFinderViewModel
import kotlinx.coroutines.FlowPreview

object HomeRoute : NavRoute<HomeViewModel> {

    override val route: String
        get() = BottomNavScreens.Home.route

    @Composable
    override fun Content(viewModel: HomeViewModel) {
        HomeScreen(homeViewModel = viewModel, editDeliveryAddressViewModel = hiltViewModel())
    }

    @Composable
    override fun viewModel(): HomeViewModel = hiltViewModel()

    override fun isRootTab(): Boolean = true
}

object BagRoute : NavRoute<BagViewModel> {

    override val route: String
        get() = BottomNavScreens.Bag.route

    @Composable
    override fun Content(viewModel: BagViewModel) {
        BagScreen(
//            bagViewModel = hiltViewModel(),
            editDeliveryAddressViewModel = hiltViewModel()
        )

    }

    @Composable
    override fun viewModel(): BagViewModel = hiltViewModel()

    override fun isRootTab(): Boolean = true

    override fun getDeepLinks(): List<NavDeepLink> {
        return listOf(
            navDeepLink {
                uriPattern = "https://jetpackmo.com/bag"
                action = Intent.ACTION_VIEW
            }
        )
    }
}


object ProductDetailFromBagRoute : NavRoute<ProductDetailViewModel> {

    override val route: String
        get() = BottomNavScreens.ProductDetailFromBag.route

    @Composable
    override fun Content(viewModel: ProductDetailViewModel) {
        ProductDetailScreen(viewModel = viewModel)
    }

    @Composable
    override fun viewModel(): ProductDetailViewModel = hiltViewModel()

    override fun getArguments(): List<NamedNavArgument> {
        return BottomNavScreens.ProductDetailFromBag.navArguments()
    }
}

object ProductDetailFromVenueRoute : NavRoute<ProductDetailViewModel> {
    override val route: String
        get() = BottomNavScreens.ProductDetailModal.route

    @Composable
    override fun Content(viewModel: ProductDetailViewModel) {
        ProductDetailScreen(viewModel = viewModel)
    }

    @Composable
    override fun viewModel(): ProductDetailViewModel = hiltViewModel()
}

object VenueDetailRoute : NavRoute<VenueDetailViewModel> {
    override val route: String
        get() = BottomNavScreens.VenueDetail.route

    @Composable
    override fun Content(viewModel: VenueDetailViewModel) {
        VenueDetailScreen(viewModel)
    }

    @Composable
    override fun viewModel(): VenueDetailViewModel = hiltViewModel()
}

object MapVenueFinderRoute : NavRoute<VenueFinderViewModel> {

    override val route: String
        get() = BottomNavScreens.VenueFinder.route

    @Composable
    override fun Content(viewModel: VenueFinderViewModel) {
        VenueFinderScreen(venueFinderViewModel = viewModel)
    }

    @Composable
    override fun viewModel(): VenueFinderViewModel = hiltViewModel()

    override fun isRootTab(): Boolean = true
}