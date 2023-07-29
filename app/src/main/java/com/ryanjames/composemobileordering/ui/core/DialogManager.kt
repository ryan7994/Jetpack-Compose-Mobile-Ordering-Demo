package com.ryanjames.composemobileordering.ui.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface DialogManager {

    fun showDialog(state: AlertDialogState)
    fun hideDialog()

    val alertDialogState: StateFlow<AlertDialogState?>

}

class DialogManagerImpl : DialogManager {

    private val _alertDialogState = MutableStateFlow<AlertDialogState?>(null)
    override val alertDialogState: StateFlow<AlertDialogState?>
        get() = _alertDialogState.asStateFlow()

    override fun showDialog(state: AlertDialogState) {
        _alertDialogState.update {

            if (state is DismissibleDialogState) {
                val onDismissDialog = state.onDismissDialog
                state.copy(onDismissDialog = {
                    _alertDialogState.update { null }
                    onDismissDialog?.let { it() }
                })
            } else {
                state
            }
        }
    }

    override fun hideDialog() {
        _alertDialogState.update { null }
    }


}