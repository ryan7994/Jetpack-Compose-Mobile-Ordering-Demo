package com.ryanjames.composemobileordering.util

import android.util.Patterns

object TextFieldValidator {

    fun isUsernameValid(username: String): Boolean {
        val trimmedUsername = username.trim()
        return trimmedUsername.isNotBlank() && trimmedUsername.length >= 6
    }

    fun isPasswordValid(password: String): Boolean {
        return password.isNotBlank() && password.length >= 8
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPhoneValid(phone: String): Boolean {
        return Patterns.PHONE.matcher(phone).matches()
    }
}