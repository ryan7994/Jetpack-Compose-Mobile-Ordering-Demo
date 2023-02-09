package com.ryanjames.composemobileordering.features.signup

import com.ryanjames.composemobileordering.ui.core.AlertDialogState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

data class SignUpScreenState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val fieldsWithError: List<SignUpFormField> = listOf(),
    val alertDialogState: AlertDialogState? = null
)

sealed class SignUpFormField {
    object Username : SignUpFormField()
    object Password : SignUpFormField()
    object ConfirmPassword : SignUpFormField()
    object Email : SignUpFormField()
    object FirstName : SignUpFormField()
    object LastName : SignUpFormField()
    object PhoneNumber : SignUpFormField()
}
