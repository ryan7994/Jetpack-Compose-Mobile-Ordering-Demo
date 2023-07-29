package com.ryanjames.composemobileordering.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.collectResource
import com.ryanjames.composemobileordering.core.StringResource
import com.ryanjames.composemobileordering.features.login.LoginEvent.LoginErrorEvent
import com.ryanjames.composemobileordering.network.LoginService
import com.ryanjames.composemobileordering.network.model.Event
import com.ryanjames.composemobileordering.ui.core.DialogManager
import com.ryanjames.composemobileordering.ui.core.DismissibleDialogState
import com.ryanjames.composemobileordering.ui.core.LoadingDialogState
import com.ryanjames.composemobileordering.usecase.GetLoginStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: LoginService,
    private val dialogManager: DialogManager,
    getLoginStateUseCase: GetLoginStateUseCase
) : ViewModel(), DialogManager by dialogManager {

    private val _loginViewState = MutableStateFlow(LoginScreenState())
    val loginScreenState = _loginViewState.asStateFlow()

    private val _loginEvent: MutableStateFlow<Event<LoginEvent>> = MutableStateFlow(Event(LoginEvent.NoEvent))
    val loginEvent: StateFlow<Event<LoginEvent>>
        get() = _loginEvent

    init {
        if (getLoginStateUseCase()) {
            _loginEvent.update { Event(LoginEvent.AutoLogin) }
        }
    }

    fun onValueChange(text: String, loginFormField: LoginFormField) {
        when (loginFormField) {
            LoginFormField.Password -> _loginViewState.update { _loginViewState.value.copy(password = text.trim()) }
            LoginFormField.Username -> _loginViewState.update { _loginViewState.value.copy(username = text.trim()) }
        }
    }

    fun onClickSignIn() {
        when {
            username.isBlank() && password.isBlank() -> showBlankUsernameAndPasswordDialog()
            username.isBlank() -> showBlankUsernameDialog()
            password.isBlank() -> showBlankPasswordDialog()
            else -> login()
        }
    }

    private fun showLoggingInDialog() {
        dialogManager.showDialog(LoadingDialogState(loadingText = StringResource(id = R.string.logging_in)))
    }

    private fun showIncorrectCredentialsDialog() {
        dialogManager.showDialog(
            DismissibleDialogState(
                dialogTitle = StringResource(R.string.login_failed),
                dialogMessage = StringResource(R.string.no_match_username_password)
            )
        )
    }

    private fun showBlankUsernameDialog() {
        dialogManager.showDialog(
            DismissibleDialogState(
                dialogTitle = StringResource(R.string.login_failed),
                dialogMessage = StringResource(R.string.username_empty)
            )
        )
    }

    private fun showBlankPasswordDialog() {
        dialogManager.showDialog(
            DismissibleDialogState(
                dialogTitle = StringResource(R.string.login_failed),
                dialogMessage = StringResource(R.string.password_empty)
            )
        )
    }

    private fun showBlankUsernameAndPasswordDialog() {
        dialogManager.showDialog(
            DismissibleDialogState(
                dialogTitle = StringResource(R.string.login_failed),
                dialogMessage = StringResource(R.string.username_and_password_empty)
            )
        )
    }

    private fun login() {
        viewModelScope.launch {

            apiService.authenticate(username = username, password = password).collectResource(
                onLoading = {
                    showLoggingInDialog()
                },
                onSuccess = {
                    dialogManager.hideDialog()
                    _loginEvent.update {
                        Event(LoginEvent.LoginSuccess)
                    }
                },
                onError = {
                    _loginEvent.update {
                        Event(LoginErrorEvent(LoginError.LoginFailed))
                    }
                    showIncorrectCredentialsDialog()
                }
            )
        }
    }

    private val username
        get() = loginScreenState.value.username

    private val password
        get() = loginScreenState.value.password

}