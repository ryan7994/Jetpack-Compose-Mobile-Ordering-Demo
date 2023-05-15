package com.ryanjames.composemobileordering.navigation

import com.ryanjames.composemobileordering.features.bottomnav.BottomNavTabs
import java.util.*

sealed class NavigationState {

    /**
     * @param id is used so that multiple instances of the same route will trigger multiple navigation calls.
     */

    object Idle : NavigationState()

    data class NavigateToRoute(val route: String, val id: String = UUID.randomUUID().toString()) :
        NavigationState()

    data class NavigateUp(val id: String = UUID.randomUUID().toString()) : NavigationState()

    data class NavigateToAnotherTab(val tab: BottomNavTabs, val popUpToRoute: String? = null, val destinationRoute: String?) : NavigationState()

    object PopBackStack: NavigationState()
}