package com.ryanjames.composemobileordering.navigation

import androidx.annotation.VisibleForTesting
import com.ryanjames.composemobileordering.features.bottomnav.BottomNavTabs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Navigator to use when initiating navigation from a ViewModel.
 */
interface RouteNavigator {
    fun onNavigated(state: NavigationState)
    fun navigateUp()
    fun navigateToRoute(route: String)
    fun navigateToAnotherTab(tab: BottomNavTabs, popUpToRoute: String? = null, destinationRoute: String?, popAllChildrenTabs: Boolean = false)
    fun popBackStack()

    val navigationState: StateFlow<NavigationState>
}

class MyRouteNavigator : RouteNavigator {

    /**
     * Note that I'm using a single state here, not a list of states. As a result, if you quickly
     * update the state multiple times, the view will only receive and handle the latest state,
     * which is fine for my use case.
     */
    override val navigationState: MutableStateFlow<NavigationState> =
        MutableStateFlow(NavigationState.Idle)

    override fun onNavigated(state: NavigationState) {
        // clear navigation state, if state is the current state:
        navigationState.compareAndSet(state, NavigationState.Idle)
    }

    override fun navigateUp() = navigate(NavigationState.NavigateUp())

    override fun navigateToRoute(route: String) = navigate(NavigationState.NavigateToRoute(route))

    override fun navigateToAnotherTab(tab: BottomNavTabs, popUpToRoute: String?, destinationRoute: String?, popAllChildrenTabs: Boolean) =
        navigate(NavigationState.NavigateToAnotherTab(tab, popUpToRoute, destinationRoute))

    override fun popBackStack() {
        navigate(NavigationState.PopBackStack)
    }

    @VisibleForTesting
    fun navigate(state: NavigationState) {
        navigationState.value = state
    }
}