package com.ryanjames.composemobileordering.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.core.LoginManager
import com.ryanjames.composemobileordering.core.StringResource
import com.ryanjames.composemobileordering.features.login.LoginEvent.LoginErrorEvent
import com.ryanjames.composemobileordering.network.ApiService
import com.ryanjames.composemobileordering.network.model.Event
import com.ryanjames.composemobileordering.ui.core.AlertDialogState
import com.ryanjames.composemobileordering.ui.core.LoadingDialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    loginManager: LoginManager
) : ViewModel() {

    private val _loginViewState = MutableStateFlow(LoginScreenState())
    val loginScreenState = _loginViewState.asStateFlow()

    private val _loginEvent: MutableStateFlow<Event<LoginEvent>> = MutableStateFlow(Event(LoginEvent.NoEvent))
    val loginEvent: StateFlow<Event<LoginEvent>>
        get() = _loginEvent

    init {
        if (loginManager.isLoggedIn()) {
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

    private fun dismissDialog() {
        _loginViewState.update { _loginViewState.value.copy(alertDialogState = null) }
    }

    private fun showLoggingInDialog() {
        _loginViewState.update {
            _loginViewState.value.copy(
                alertDialogState = LoadingDialogState(loadingText = StringResource(id = R.string.logging_in))
            )
        }
    }

    private fun showIncorrectCredentialsDialog() {
        _loginViewState.update {
            _loginViewState.value.copy(
                alertDialogState = AlertDialogState(
                    title = StringResource(R.string.login_failed),
                    message = StringResource(R.string.no_match_username_password),
                    onDismiss = this@LoginViewModel::dismissDialog
                )
            )
        }
    }

    private fun showBlankUsernameDialog() {
        _loginViewState.update {
            _loginViewState.value.copy(
                alertDialogState = AlertDialogState(
                    title = StringResource(R.string.login_failed),
                    message = StringResource(R.string.username_empty),
                    onDismiss = this@LoginViewModel::dismissDialog
                )
            )
        }
    }

    private fun showBlankPasswordDialog() {
        _loginViewState.update {
            _loginViewState.value.copy(
                alertDialogState = AlertDialogState(
                    title = StringResource(R.string.login_failed),
                    message = StringResource(R.string.password_empty),
                    onDismiss = this@LoginViewModel::dismissDialog
                )
            )
        }
    }

    private fun showBlankUsernameAndPasswordDialog() {
        _loginViewState.update {
            _loginViewState.value.copy(
                alertDialogState = AlertDialogState(
                    title = StringResource(R.string.login_failed),
                    message = StringResource(R.string.username_and_password_empty),
                    onDismiss = this@LoginViewModel::dismissDialog
                )
            )
        }
    }

    private fun login() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                apiService.authenticate(username = username, password = password).collect {

                    it.doOnLoading {
                        showLoggingInDialog()
                    }

                    it.doOnSuccess {
                        _loginEvent.update { Event(LoginEvent.LoginSuccess) }
                    }

                    it.doOnError {
                        _loginEvent.update { Event(LoginErrorEvent(LoginError.LoginFailed)) }
                        showIncorrectCredentialsDialog()
                    }

                }
            }

        }
    }

    private val username
        get() = loginScreenState.value.username

    private val password
        get() = loginScreenState.value.password

}