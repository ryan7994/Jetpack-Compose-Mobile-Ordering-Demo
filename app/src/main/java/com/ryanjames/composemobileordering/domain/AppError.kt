package com.ryanjames.composemobileordering.domain

data class AppError(
    val apiErrorMessage: String? = null,
    val apiErrorCode: Int? = null,
    val userDefinedErrorCode: String? = null
)
