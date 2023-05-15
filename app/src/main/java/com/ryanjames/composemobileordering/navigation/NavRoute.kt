package com.ryanjames.composemobileordering.navigation

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.composable

/**
 * A route the app can navigate to.
 */
interface NavRoute<T : RouteNavigator> {

    val route: String

    /**
     * Returns the screen's content.
     */
    @Composable
    fun Content(viewModel: T)

    /**
     * Returns the screen's ViewModel. Needs to be overridden so that Hilt can generate code for the factory for the ViewModel class.
     */
    @Composable
    fun viewModel(): T

    /**
     * Override when this page uses arguments.
     *
     * We do it here and not in the [NavigationComponent to keep it centralized]
     */
    fun getArguments(): List<NamedNavArgument> = listOf()

    fun isRootTab(): Boolean = false

    fun getDeepLinks(): List<NavDeepLink> = listOf()

    /**
     * Generates the composable for this route.
     */
    fun composable(
        builder: NavGraphBuilder,
        navHostController: NavHostController
    ) {
        builder.composable(
            route = route,
            arguments = getArguments(),
            deepLinks = getDeepLinks()
        ) {

            val viewModel = viewModel()
            val viewStateAsState by viewModel.navigationState.collectAsState()

            LaunchedEffect(viewStateAsState) {
                Log.d("Nav", "${this@NavRoute} updateNavigationState to $viewStateAsState")
                updateNavigationState(navHostController, viewStateAsState, viewModel::onNavigated)
            }

            BackHandler(enabled = isRootTab()) {}
            Content(viewModel)
        }
    }

    /**
     * Navigates to viewState.
     */
    private fun updateNavigationState(
        navHostController: NavController,
        navigationState: NavigationState,
        onNavigated: (navState: NavigationState) -> Unit,
    ) {
        when (navigationState) {
            is NavigationState.NavigateToRoute -> {
                navHostController.navigate(navigationState.route)
                onNavigated(navigationState)
            }
            is NavigationState.PopBackStack -> {
                navHostController.popBackStack()
                onNavigated(navigationState)
            }
            is NavigationState.NavigateUp -> {
                navHostController.navigateUp()
            }
            is NavigationState.Idle -> {
            }
            is NavigationState.NavigateToAnotherTab -> {
                navHostController.navigate(navigationState.tab.tabRoute) {
                    val id = navHostController.graph.findStartDestination().id

                    popUpTo(id) {
                        saveState = true
                    }

                    launchSingleTop = true
                    restoreState = true
                }

                // If destination is already in the back stack, popUpTo destination


                if (navigationState.destinationRoute != null && navHostController.findDestination(navigationState.destinationRoute) != null) {
                    navHostController.popBackStack(navigationState.destinationRoute, inclusive = false, saveState = false)
                } else if (navigationState.popUpToRoute != null && navHostController.findDestination(navigationState.popUpToRoute) != null) {
                    navHostController.popBackStack(navigationState.popUpToRoute, false)
                }

                if (navigationState.destinationRoute != null && navHostController.currentDestination?.route != navigationState.destinationRoute) {
                    navHostController.navigate(navigationState.destinationRoute)
                }

                onNavigated(navigationState)
            }
        }
    }
}

fun <T> SavedStateHandle.getOrThrow(key: String): T =
    get<T>(key) ?: throw IllegalArgumentException(
        "Mandatory argument $key missing in arguments."
    )