package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase6

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class RetryNetworkRequestViewModel(
    private val api: MockApi = mockApi(),
) : BaseViewModel<UiState>() {
    fun performNetworkRequest() {
        uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val versions =
                    retry(2) {
                        api.getRecentAndroidVersions()
                    }
                uiState.value = UiState.Success(versions)
            } catch (_: Exception) {
                uiState.value = UiState.Error("Something went wrong!")
            }
        }
    }

    private suspend fun <T> retry(
        noOfRetries: Int,
        initialDelayMillis: Long = 100,
        maxDelayMillis: Long = 1000,
        factor: Double = 2.0,
        block: suspend () -> T,
    ): T {
        var currentDelay = initialDelayMillis
        repeat(noOfRetries) {
            try {
                return block()
            } catch (_: Exception) {
                // log exception
                Timber.e("Something went wrong!")
            }
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMillis)
        }
        return block()
    }
}
