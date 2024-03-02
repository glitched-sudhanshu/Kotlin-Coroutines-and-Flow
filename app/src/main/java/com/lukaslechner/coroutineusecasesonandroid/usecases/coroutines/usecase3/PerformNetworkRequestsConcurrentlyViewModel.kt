package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase3

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class PerformNetworkRequestsConcurrentlyViewModel(
    private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequestsSequentially() {
        uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val features3 = mockApi.getAndroidVersionFeatures(27)
                val features2 = mockApi.getAndroidVersionFeatures(28)
                val features = mockApi.getAndroidVersionFeatures(29)
                uiState.value = UiState.Success(listOf(features, features2, features3))
            } catch (_: Exception) {
                uiState.value = UiState.Error("Something went wrong!")
            }
        }
    }

    fun performNetworkRequestsConcurrently() {
        uiState.value = UiState.Loading
        val features3 = viewModelScope.async { mockApi.getAndroidVersionFeatures(27) }
        val features2 = viewModelScope.async { mockApi.getAndroidVersionFeatures(28) }
        val features = viewModelScope.async { mockApi.getAndroidVersionFeatures(29) }

        //aysnc does not throw exception, but holds it reference as await is called at some point in the future
        //and re-throws it when await is called.
        viewModelScope.launch {
            try {
                val result = awaitAll(features, features2, features3)
                uiState.value = UiState.Success(result)
            } catch (_: Exception) {
                uiState.value = UiState.Error("Something went wrong!")
            }
        }
    }
}