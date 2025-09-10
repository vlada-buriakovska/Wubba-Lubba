package com.vladabur.wubbalubba.presentation.common

data class BaseUiState(
    val error: String? = null,
    val isConnectionError: Boolean? = null,
    val isLoading: Boolean = false
)
