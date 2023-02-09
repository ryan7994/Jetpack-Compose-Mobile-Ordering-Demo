package com.ryanjames.composemobileordering.network.model.request

data class EnrollRequest(
    val username: String,
    val password: String,
    val phoneNumber: String,
    val phoneNumberCountryCode: String,
    val firstName: String,
    val lastName: String,
    val email: String
)