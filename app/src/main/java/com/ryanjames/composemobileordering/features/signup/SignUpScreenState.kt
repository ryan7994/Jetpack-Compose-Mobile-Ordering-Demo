package com.ryanjames.composemobileordering.features.signup

data class SignUpScreenState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val fieldsWithError: List<SignUpFormField> = listOf()
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
