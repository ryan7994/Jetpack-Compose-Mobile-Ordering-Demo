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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ryanjames.composemobileordering.core.LoginManager
import com.ryanjames.composemobileordering.core.SnackbarManager
import com.ryanjames.composemobileordering.features.login.LoginActivity
import com.ryanjames.composemobileordering.navigation.*
import com.ryanjames.composemobileordering.ui.core.CustomSnackbar
import com.ryanjames.composemobileordering.ui.core.Dialog
import com.ryanjames.composemobileordering.ui.core.DialogManager
import com.ryanjames.composemobileordering.ui.core.customTextSelectionColors
import com.ryanjames.composemobileordering.ui.theme.AppTheme
import com.ryanjames.composemobileordering.ui.theme.CoralRed
import com.ryanjames.composemobileordering.ui.theme.MyComposeAppTheme
import com.ryanjames.composemobileordering.ui.theme.Typography
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

    @Inject
    lateinit var snackbarManager: SnackbarManager

    @Inject
    lateinit var dialogManager: DialogManager

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
                    Dialog(alertDialogState = dialogManager.alertDialogState.collectAsState().value)
                    Snackbar()
                }
            }
        }
    }

    @Composable
    private fun Snackbar() {
        val context = LocalContext.current
        val snackbarHostState = LocalSnackbarHostState.current
        LaunchedEffect(Unit) {
            snackbarManager.snackbarFlow.collect { event ->
                event?.handleSuspending { snackbarData ->
                    val snackbarMessage = context.getString(snackbarData.content.message.id)
                    snackbarHostState.showSnackbar(snackbarMessage)
                }
            }
        }
    }

    private fun NavGraphBuilder.bagTapGraph(navController: NavHostController) {

        navigation(startDestination = BottomNavScreens.Bag.route, route = BottomNavTabs.BagTab.tabRoute) {
            BagRoute.composable(this, navController)
            ProductDetailFromBagRoute.composable(this, navController)
        }
    }

    private fun NavGraphBuilder.mapTabGraph(navController: NavHostController) {
        navigation(startDestination = BottomNavScreens.VenueFinder.route, route = BottomNavTabs.MapTab.tabRoute) {
            MapVenueFinderRoute.composable(this, navController)

        }
    }

    private fun NavGraphBuilder.browseTabGraph(navController: NavHostController) {

        navigation(startDestination = BottomNavScreens.Home.route, route = BottomNavTabs.BrowseTab.tabRoute) {
            HomeRoute.composable(this, navController)
            VenueDetailRoute.composable(this, navController)
            ProductDetailFromVenueRoute.composable(this, navController)
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
            this.browseTabGraph(navController = navController)
            this.bagTapGraph(navController = navController)
            this.mapTabGraph(navController = navController)
        }

    }
}


