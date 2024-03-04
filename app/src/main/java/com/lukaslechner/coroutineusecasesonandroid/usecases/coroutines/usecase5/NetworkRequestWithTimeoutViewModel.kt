package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase5

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

class NetworkRequestWithTimeoutViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequest(timeout: Long) {
        uiState.value = UiState.Loading
//        requestWithTimeout(timeout)
        requestWithTimeoutOrNull(timeout)

    }

    private fun requestWithTimeout(timeout: Long) {
        viewModelScope.launch {
            try {
                val versions = withTimeout(timeout) { api.getRecentAndroidVersions() }
                uiState.value = UiState.Success(versions)
            } catch (timeoutException: TimeoutCancellationException) {
                uiState.value = UiState.Error("Network timeout!")
            } catch (e: Exception) {
                uiState.value = UiState.Error("Something went wrong!")
            }
        }
    }

    private fun requestWithTimeoutOrNull(timeout: Long) {
        viewModelScope.launch {
            try {
                val versions = withTimeoutOrNull(timeout) { api.getRecentAndroidVersions() }
                if (versions != null)
                    uiState.value = UiState.Success(versions)
                else uiState.value = UiState.Error("Network timeout!")
            } catch (e: Exception) {
                uiState.value = UiState.Error("Something went wrong!")
            }
        }
    }
}