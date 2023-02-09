package com.ryanjames.composemobileordering.features.bottomnav

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ryanjames.composemobileordering.core.LoginManager
import com.ryanjames.composemobileordering.features.bag.BagScreen
import com.ryanjames.composemobileordering.features.productdetail.ProductDetailScreen
import com.ryanjames.composemobileordering.features.venuedetail.VenueDetailScreen
import com.ryanjames.composemobileordering.features.venuemapfinder.VenueFinderScreen
import com.ryanjames.composemobileordering.ui.core.CustomSnackbar
import com.ryanjames.composemobileordering.ui.core.customTextSelectionColors
import com.ryanjames.composemobileordering.features.home.HomeScreen
import com.ryanjames.composemobileordering.features.login.LoginActivity
import com.ryanjames.composemobileordering.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }
val LocalCoroutineScope = compositionLocalOf<CoroutineScope> { error("No coroutine scope provided") }

@ExperimentalPagerApi
@ExperimentalMaterialApi
@AndroidEntryPoint
class BottomNavActivity : ComponentActivity() {

    @Inject
    lateinit var loginManager: LoginManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            loginManager.logOutStateFlow.collect { event ->
                event.handle { shouldLogOut ->
                    if (shouldLogOut) {
                        startActivity(Intent(this@BottomNavActivity, LoginActivity::class.java))
                        finish()
                    }
                }

            }
        }


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
        val bottomNavTabs = listOf(BottomNavTabs.BrowseTab, BottomNavTabs.MapTab, BottomNavTabs.BagTab)

        val scaffoldState = rememberScaffoldState()
        val coroutineScope = rememberCoroutineScope()
        CompositionLocalProvider(
            LocalSnackbarHostState provides scaffoldState.snackbarHostState,
            LocalCoroutineScope provides coroutineScope,
            LocalTextSelectionColors provides customTextSelectionColors
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
                Surface(modifier = Modifier.padding(innerPadding)) {
                    BottomNavScreenNavigationConfig(
                        navController = navController,
                    )
                }
            }
        }


    }

    private fun NavGraphBuilder.bagGraph(navController: NavController) {

        navigation(startDestination = BottomNavScreens.Bag.route, route = BottomNavTabs.BagTab.tabRoute) {
            composable(
                route = BottomNavScreens.Bag.route,
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "https://jetpackmo.com/bag"
                        action = Intent.ACTION_VIEW
                    }
                ),
                arguments = listOf()
            ) {
                BagScreen(bagViewModel = hiltViewModel(),
                    editDeliveryAddressViewModel = hiltViewModel(),
                    onClickAddMoreItems = { venueId ->
                        navController.navigateToAnotherTab(BottomNavTabs.BrowseTab, BottomNavScreens.Home.route)
                        navController.navigate(BottomNavScreens.VenueDetail.routeWithArgs(venueId))
                    },
                    onClickLineItem = { lineItemId ->
                        navController.navigate(BottomNavScreens.ProductDetailFromBag.routeWithArgs(lineItemId = lineItemId))
                    },
                    onClickBrowseRestaurants = {
                        navController.navigateToAnotherTab(BottomNavTabs.BrowseTab, BottomNavScreens.Home.route)
                    })
            }

            composable(
                BottomNavScreens.ProductDetailFromBag.route,
                arguments = BottomNavScreens.ProductDetailFromBag.navArguments()
            ) {
                NavigateToProductDetailScreen(
                    onSuccessfulAddOrUpdate = {
                        navController.popBackStack(BottomNavScreens.Bag.route, false)
                    },
                    onLoadFail = {
                        navController.popBackStack()
                    })
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

    @Composable
    private fun NavigateToProductDetailScreen(
        onSuccessfulAddOrUpdate: () -> Unit,
        onLoadFail: () -> Unit
    ) {
        ProductDetailScreen(
            viewModel = hiltViewModel(),
            onSuccessfulAddOrUpdate = onSuccessfulAddOrUpdate,
            onLoadFail = onLoadFail
        )
    }

    private fun NavGraphBuilder.mapGraph(navController: NavController) {
        navigation(startDestination = BottomNavScreens.VenueFinder.route, route = BottomNavTabs.MapTab.tabRoute) {
            composable(BottomNavScreens.VenueFinder.route) {
                VenueFinderScreen(hiltViewModel()) { venueId ->
                    navController.navigateToAnotherTab(BottomNavTabs.BrowseTab, BottomNavScreens.Home.route)
                    navController.navigate(BottomNavScreens.VenueDetail.routeWithArgs(venueId))
                }
            }
        }
    }

    private fun NavGraphBuilder.browseGraph(
        navController: NavController,
    ) {

        navigation(startDestination = BottomNavScreens.Home.route, route = BottomNavTabs.BrowseTab.tabRoute) {

            composable(BottomNavScreens.Home.route) {
                HomeScreen(homeViewModel = hiltViewModel(),
                    editDeliveryAddressViewModel = hiltViewModel(),
                    onClickCard = { venueId ->
                        navController.navigate(BottomNavScreens.VenueDetail.routeWithArgs(venueId))
                    })
            }

            composable(BottomNavScreens.VenueDetail.route) {
                NavigateToVenueDetailScreen(navController = navController)
            }

            composable(BottomNavScreens.ProductDetailModal.route) {
                NavigateToProductDetailScreen(onSuccessfulAddOrUpdate = {
                    navController.popBackStack(BottomNavScreens.VenueDetail.route, false)
                }, onLoadFail = {
                    navController.popBackStack()
                })
            }
        }
    }

    private fun NavController.navigateToAnotherTab(tab: BottomNavTabs, popUpToRoute: String? = null) {
        navigate(tab.tabRoute) {
            val id = graph.findStartDestination().id

            popUpTo(id) {
                saveState = true
            }

            launchSingleTop = true
            restoreState = true
        }
        if (popUpToRoute != null) {
            this.popBackStack(popUpToRoute, false, false)
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
                        val currentTabRoute = backStackEntry?.destination?.parent?.route
                        if (tab.tabRoute != currentTabRoute) {
                            navController.navigateToAnotherTab(tab)
                        }
                    },
                    label = {
                        Text(
                            stringResource(id = tab.labelResId),
                            style = Typography.bodyMedium
                        )
                    },
                    alwaysShowLabel = false,
                    selected = isSelected,
                    icon = {
                        tab.drawableId?.let { Icon(painterResource(id = tab.drawableId), stringResource(id = tab.labelResId), modifier = Modifier.size(24.dp)) }
                        tab.icon?.let { Icon(tab.icon, stringResource(id = tab.labelResId)) }
                    },
                )
            }

        }
    }


    @Composable
    private fun BottomNavScreenNavigationConfig(
        navController: NavHostController
    ) {

        NavHost(navController, startDestination = BottomNavTabs.BrowseTab.tabRoute) {
            this.browseGraph(navController = navController)
            this.bagGraph(navController = navController)
            this.mapGraph(navController = navController)
        }

    }
}


