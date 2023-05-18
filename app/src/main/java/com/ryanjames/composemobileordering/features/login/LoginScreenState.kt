package com.ryanjames.composemobileordering.features.login


data class LoginScreenState(
    val username: String = "",
    val password: String = ""
)

sealed class LoginFormField {
    object Username : LoginFormField()
    object Password : LoginFormField()
}

sealed class LoginEvent {
    object NoEvent : LoginEvent()
    object LoginSuccess : LoginEvent()
    data class LoginErrorEvent(val error: LoginError) : LoginEvent()
    object AutoLogin : LoginEvent()
}


sealed class LoginError {
    object EmptyUsername : LoginError()
    object EmptyPassword : LoginError()
    object EmptyUsernameAndPassword : LoginError()
    object LoginFailed : LoginError()
}


