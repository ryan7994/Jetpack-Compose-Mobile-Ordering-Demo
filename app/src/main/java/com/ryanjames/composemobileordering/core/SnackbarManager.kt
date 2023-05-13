package com.ryanjames.composemobileordering.core

import com.ryanjames.composemobileordering.network.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class SnackbarManager {

    val snackbarFlow = MutableStateFlow<Event<SnackbarData>?>(null)

    fun showSnackbar(snackbarData: SnackbarData) {
        snackbarFlow.update { Event(snackbarData) }
    }

}

data class SnackbarContent(val message: StringResource)

data class SnackbarData(
    val eventId: String,
    val content: SnackbarContent
)

val EVENT_SUCCESSFUL_BAG_UPDATE = "successful_bag_update"
val EVENT_SUCCESSFUL_ITEM_REMOVAL = "successful_item_removal"