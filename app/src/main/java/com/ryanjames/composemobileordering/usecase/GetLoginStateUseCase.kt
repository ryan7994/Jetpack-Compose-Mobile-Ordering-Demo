package com.ryanjames.composemobileordering.usecase

import android.content.SharedPreferences
import com.ryanjames.composemobileordering.constants.SharedPrefsKeys

class GetLoginStateUseCase(private val sharedPreferences: SharedPreferences) {

    operator fun invoke(): Boolean {
        return sharedPreferences.getString(SharedPrefsKeys.KEY_AUTH_TOKEN, null) != null
    }

}