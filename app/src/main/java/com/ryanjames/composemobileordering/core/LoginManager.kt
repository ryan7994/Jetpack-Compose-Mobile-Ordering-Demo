package com.ryanjames.composemobileordering.core

import android.content.SharedPreferences
import com.ryanjames.composemobileordering.constants.SharedPrefsKeys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class LoginManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    private val logOutChannel = MutableStateFlow(true)
    val logOutStateFlow: StateFlow<Boolean>
        get() = logOutChannel

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getString(SharedPrefsKeys.KEY_AUTH_TOKEN, null) != null
    }

    fun logOut() {
        logOutChannel.value = true
        sharedPreferences.edit()
            .remove(SharedPrefsKeys.KEY_AUTH_TOKEN)
            .remove(SharedPrefsKeys.KEY_REFRESH_TOKEN)
            .apply()
    }

}