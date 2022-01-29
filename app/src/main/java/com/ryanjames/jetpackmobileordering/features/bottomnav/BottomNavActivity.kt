package com.ryanjames.jetpackmobileordering.features.bottomnav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ryanjames.jetpackmobileordering.features.bag.BagScreen
import com.ryanjames.jetpackmobileordering.features.productdetail.ProductDetailScreen
import com.ryanjames.jetpackmobileordering.features.venuedetail.VenueDetailScreen
import com.ryanjames.jetpackmobileordering.ui.core.CustomSnackbar
import com.ryanjames.jetpackmobileordering.ui.screens.HomeScreen
import com.ryanjames.jetpackmobileordering.ui.theme.AppTheme
import com.ryanjames.jetpackmobileordering.ui.theme.CoralRed
import com.ryanjames.jetpackmobileordering.ui.theme.FreeSans
import com.ryanjames.jetpackmobileordering.ui.theme.MyComposeAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }
val LocalCoroutineScope = compositionLocalOf<CoroutineScope> { error("No coroutine scope provided") }

@AndroidEntryPoint
class BottomNavActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyComposeAppTheme {
                // ToDo: Replace if directly supported by Jetpack Compose
                val systemUiController = rememberSystemUiController()
                val statusBarColor = AppTheme.colors.materialColors.primary
                SideEffect {
                    systemUiController.setSystemBarsColor(statusBarColor, darkIcons = false)
                }

                BottomNavScreen()
            }

        }

    }

    @Composable
    fun BottomNavScreen() {

        val navController = rememberNavController()
        val bottomNavTabs = listOf(BottomNavTabs.BrowseTab, BottomNavTabs.BagTab)

        val scaffoldState = rememberScaffoldState()
        val coroutineScope = rememberCoroutineScope()
        CompositionLocalProvider(
            LocalSnackbarHostState provides scaffoldState.snackbarHostState,
            LocalCoroutineScope provides coroutineScope
        ) {
            Scaffold(scaffoldState = scaffoldState,
                bottomBar = {
                    BottomNavBar(navController = navController, items = bottomNavTabs)
                },
                snackbarHost = { snackbarHostState ->
                    SnackbarHost(snackbarHostState) { data ->
                        CustomSnackbar(data)
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    BottomNavScreenNavigationConfig(
                        navController = navController,
                    )
                }
            }
        }


    }

    @ExperimentalMaterialApi
    fun NavGraphBuilder.bagGraph(navController: NavController) {

        navigation(startDestination = BottomNavScreens.Bag.route, route = BottomNavTabs.BagTab.tabRoute) {
            composable(BottomNavScreens.Bag.route) {
                BagScreen(hiltViewModel(),
                    onClickAddMoreItems = { venueId ->
                        navController.navigate(BottomNavScreens.VenueDetailFromBag.routeWithArgs(venueId))
                    },
                    onClickLineItem = { lineItemId ->
                        navController.navigate(BottomNavScreens.ProductDetailFromBag.routeWithArgs(lineItemId = lineItemId))
                    })
                BackHandler {}
            }

            composable(
                BottomNavScreens.ProductDetailFromBag.route,
                arguments = BottomNavScreens.ProductDetailFromBag.navArguments()
            ) {
                NavigateToProductDetailScreen(navController = navController)
            }

            composable(BottomNavScreens.VenueDetailFromBag.route) { backStackEntry ->
                VenueDetailScreen(
                    onClickMenuItemCard = { productId, venueId ->
                        navController.navigate(BottomNavScreens.ProductDetailFromBag.routeWithArgs(productId, venueId))
                    },
                    venueDetailViewModel = hiltViewModel(),
                    onClickUpBtn = { navController.popBackStack() }
                )
            }
        }
    }

    @Composable
    private fun NavigateToVenueDetailScreen(navController: NavController) {
        VenueDetailScreen(
            onClickMenuItemCard = { productId, venueId ->
                navController.navigate(BottomNavScreens.ProductDetailModal.routeWithArgs(productId, venueId))
            },
            venueDetailViewModel = hiltViewModel(),
            onClickUpBtn = { navController.popBackStack() }
        )
    }

    @ExperimentalMaterialApi
    @Composable
    private fun NavigateToProductDetailScreen(navController: NavController) {
        ProductDetailScreen(viewModel = hiltViewModel(),
            onSuccessfulAddOrUpdate = {
                navController.popBackStack()
            })
    }

    @ExperimentalMaterialApi
    fun NavGraphBuilder.browseGraph(
        navController: NavController,
    ) {

        navigation(startDestination = BottomNavScreens.Home.route, route = BottomNavTabs.BrowseTab.tabRoute) {

            composable(BottomNavScreens.Home.route) {
                HomeScreen(viewModel = hiltViewModel(),
                    onClickCard = { venueId ->
                        navController.navigate(BottomNavScreens.VenueDetail.routeWithArgs(venueId))
                    })
            }

            composable(BottomNavScreens.VenueDetail.route) { backStackEntry ->
                NavigateToVenueDetailScreen(navController = navController)
            }

            composable(BottomNavScreens.ProductDetailModal.route) {
                NavigateToProductDetailScreen(navController = navController)
            }
        }
    }


    @Composable
    fun BottomNavBar(
        navController: NavHostController,
        items: List<BottomNavTabs>
    ) {
        BottomNavigation(backgroundColor = AppTheme.colors.bottomNavBackground) {

            val backStackEntry by navController.currentBackStackEntryAsState()
            items.forEach { tab ->
                val isSelected = backStackEntry?.destination?.hierarchy?.any { it.route == tab.tabRoute } == true

                BottomNavigationItem(
                    selectedContentColor = CoralRed,
                    unselectedContentColor = AppTheme.colors.darkTextColor,
                    onClick = {
                        navController.navigate(tab.tabRoute) {
                            val id = navController.graph.findStartDestination().id

                            popUpTo(id) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }

                    },
                    label = {
                        Text(
                            stringResource(id = tab.labelResId),
                            fontFamily = FreeSans,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    alwaysShowLabel = true,
                    selected = isSelected,
                    icon = { tab.icon?.let { Icon(tab.icon, stringResource(id = tab.labelResId)) } },
                )
            }

        }
    }

    @OptIn(
        ExperimentalMaterialApi::class,
    )
    @Composable
    private fun BottomNavScreenNavigationConfig(
        navController: NavHostController
    ) {

        NavHost(navController, startDestination = BottomNavTabs.BrowseTab.tabRoute) {
            this.browseGraph(navController = navController)
            this.bagGraph(navController = navController)
        }

    }
}


