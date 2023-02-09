package com.ryanjames.composemobileordering.network.model.response

data class EnrollResponse(
    val userId: String,
    val username: String,
    val phoneNumber: String,
    val phoneNumberCountryCode: String,
    val firstName: String,
    val lastName: String,
    val email: String
)
