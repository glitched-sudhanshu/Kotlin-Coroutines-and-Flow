package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase7

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import timber.log.Timber

class TimeoutAndRetryViewModel(
    private val api: MockApi = mockApi(),
) : BaseViewModel<UiState>() {
    fun performNetworkRequest() {
        uiState.value = UiState.Loading
        val numberOfRetries = 2
        val timeout = 1000L

        // TODO: Exercise 3
        // switch to branch "coroutine_course_full" to see solution

        // run api.getAndroidVersionFeatures(27) and api.getAndroidVersionFeatures(28) in parallel
        val deferred27 =
            viewModelScope.async {
                retryWithTimeout { api.getAndroidVersionFeatures(27) }
            }
        val deferred28 =
            viewModelScope.async {
                retryWithTimeout { api.getAndroidVersionFeatures(28) }
            }
        try {
            viewModelScope.launch {
                coroutineScope {
                    val list =
                        listOf(deferred27, deferred28).awaitAll()
                    uiState.value = UiState.Success(list)
                }
            }
        } catch (_: Exception) {
            uiState.value = UiState.Error("Something went wrong!")
        }
    }
}

private suspend fun <T> retryWithTimeout(
    noOfRetries: Int = 2,
    timeoutMillis: Long = 1000,
    block: suspend () -> T,
) = retry(noOfRetries) {
    withTimeout(timeoutMillis) {
        block()
    }
}

private suspend fun <T> retry(
    noOfRetries: Int = 2,
    initialDelayMillis: Long = 100,
    maxDelayMillis: Long = 1000,
    factor: Double = 2.0,
    block: suspend () -> T,
): T {
    var currentDelay = initialDelayMillis
    repeat(noOfRetries) {
        try {
            return block()
        } catch (e: TimeoutCancellationException) {
            Timber.e("Network timeout $e")
        } catch (e: Exception) {
            Timber.e(e)
        }
        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMillis)
    }
    return block()
}
