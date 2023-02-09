package com.ryanjames.composemobileordering.core

import androidx.lifecycle.ViewModel
import com.ryanjames.composemobileordering.ui.core.AlertDialogState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<State>(val initialState: State) : ViewModel() {


    private val _state: MutableStateFlow<State> = MutableStateFlow(initialState)
    val state: StateFlow<State>
        get() = _state

    private val _dialogState: MutableStateFlow<AlertDialogState?> = MutableStateFlow(null)
    val dialogState: StateFlow<AlertDialogState?>
        get() = _dialogState

    protected fun hideDialog() {
        _dialogState.update { null }
    }

    protected fun showDialog(dialogState: AlertDialogState) {
        _dialogState.update { dialogState }
    }

    protected fun updateState(currentState: (State) -> State) {
        _state.update {
            currentState.invoke(state.value)
        }
    }

}