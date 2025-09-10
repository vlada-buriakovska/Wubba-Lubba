package com.vladabur.wubbalubba.presentation.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


open class BaseViewModel : ViewModel() {
    private val managerBaseState = MutableStateFlow(BaseUiState())
    val baseUiState: StateFlow<BaseUiState> = managerBaseState.asStateFlow()

    private val failedRequests = mutableListOf<() -> Unit>()

    override fun onCleared() {
        super.onCleared()
        failedRequests.clear()
    }

    protected fun retry() {
        failedRequests.forEach {
            it.invoke()
        }
        failedRequests.clear()
        consumeConnectionError()
    }

    protected fun handleOnLoading(isLoading: Boolean) {
        managerBaseState.update {
            it.copy(isLoading = isLoading)
        }
    }

    protected fun handleOnError(error: Exception) {
        error.printStackTrace()
        managerBaseState.update {
            it.copy(error = error.localizedMessage)
        }
    }

    protected fun handleOnConnectionError(failedRequest: () -> Unit) {
        failedRequests.add(failedRequest)
        managerBaseState.update {
            it.copy(isConnectionError = true)
        }
    }

    protected fun consumeConnectionError() {
        managerBaseState.update {
            it.copy(isConnectionError = null)
        }
    }

    protected fun consumeError() {
        managerBaseState.update {
            it.copy(error = null)
        }
    }
}