package com.ryanjames.composemobileordering.core

import android.content.SharedPreferences
import com.ryanjames.composemobileordering.constants.SharedPrefsKeys
import com.ryanjames.composemobileordering.network.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class LoginManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    private val logOutFlow = MutableStateFlow(Event(false))
    val logOutStateFlow: StateFlow<Event<Boolean>>
        get() = logOutFlow


    fun logOut() {
        logOutFlow.value = Event(true)
        sharedPreferences.edit()
            .remove(SharedPrefsKeys.KEY_AUTH_TOKEN)
            .remove(SharedPrefsKeys.KEY_REFRESH_TOKEN)
            .apply()
    }

}