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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ryanjames.jetpackmobileordering.R
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
import kotlinx.coroutines.launch

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }

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
        val bottomNavTabs = listOf(BottomNavScreen.Home, BottomNavScreen.Bag)

        val scaffoldState = rememberScaffoldState()
        CompositionLocalProvider(
            LocalSnackbarHostState provides scaffoldState.snackbarHostState
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

    @Composable
    fun BottomNavBar(
        navController: NavHostController,
        items: List<BottomNavScreen>
    ) {
        BottomNavigation(backgroundColor = AppTheme.colors.bottomNavBackground) {

            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            val currentTab = BottomNavScreen.getTabRoute(currentRoute ?: "")

            items.forEach { tab ->

                val isSelected = currentTab == tab.rootTab

                BottomNavigationItem(
                    selectedContentColor = CoralRed,
                    unselectedContentColor = AppTheme.colors.darkTextColor,
                    onClick = {

                        if (currentTab != tab.rootTab) {
                            val route = tab.route

                            navController.navigate(route) {
                                val id = navController.graph.findStartDestination().id

                                popUpTo(id) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true
                            }
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
                    icon = { tab.icon?.let { Icon(tab.icon, stringResource(id = tab.labelResId)) } },
                    selected = isSelected,
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
        val snackbarHostState = LocalSnackbarHostState.current
        val scope = rememberCoroutineScope()
        val itemAddedMessage = stringResource(R.string.item_added)

        NavHost(navController, startDestination = BottomNavScreen.Home.route) {

            composable(BottomNavScreen.Home.route) {
                HomeScreen(viewModel = hiltViewModel(),
                    onClickCard = { venueId ->
                        navController.navigate(BottomNavScreen.VenueDetail.routeWithArgs(venueId))
                    })
            }

            composable(BottomNavScreen.VenueDetail.route) { backStackEntry ->
                VenueDetailScreen(
                    onClickMenuItemCard = { productId, venueId ->
                        navController.navigate(BottomNavScreen.ProductDetailModal.routeWithArgs(productId, venueId))
                    },
                    venueDetailViewModel = hiltViewModel(),
                    onClickUpBtn = { navController.popBackStack() }
                )
            }

            composable(BottomNavScreen.ProductDetailModal.route) {
                ProductDetailScreen(viewModel = hiltViewModel(),
                    onSuccessfulAddOrUpdate = {
                        navController.popBackStack()
                        scope.launch {
                            snackbarHostState.currentSnackbarData
                            snackbarHostState.showSnackbar(itemAddedMessage)
                        }
                    })
            }


            composable(BottomNavScreen.Bag.route) {
                BagScreen(hiltViewModel()) { venueId ->
                    navController.navigate(BottomNavScreen.ProductDetailModal.routeWithArgs("B1000", "BUGST"))
                }
                BackHandler {}
            }
        }

    }
}


