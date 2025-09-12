package com.vladabur.wubbalubba.presentation.common.base

data class BaseUiState(
    val error: String? = null,
    val unexpectedError: String? = null,
    val isConnectionError: Boolean? = null,
    val isLoading: Boolean? = null
)
