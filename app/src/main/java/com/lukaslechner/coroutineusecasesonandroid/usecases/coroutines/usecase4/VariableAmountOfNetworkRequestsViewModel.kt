package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase4

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class VariableAmountOfNetworkRequestsViewModel(
    private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequestsSequentially() {
        uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val recentList = mockApi.getRecentAndroidVersions()
                val featuresList = recentList.map {
                    mockApi.getAndroidVersionFeatures(it.apiLevel)
                }
                uiState.value = UiState.Success(featuresList)
            } catch (_: Exception) {
                uiState.value = UiState.Error("Something went wrong!")
            }
        }
    }

    fun performNetworkRequestsConcurrently() {
        uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                coroutineScope {
                    val recentVersions = mockApi.getRecentAndroidVersions()
                    val defFeatures = recentVersions.map {
                        async { mockApi.getAndroidVersionFeatures(it.apiLevel) }
                    }
                    val features = defFeatures.awaitAll()
                    uiState.value = UiState.Success(features)
                }
            } catch (_: Exception) {
                uiState.value = UiState.Error("Something went wrong!")
            }

        }
    }
}