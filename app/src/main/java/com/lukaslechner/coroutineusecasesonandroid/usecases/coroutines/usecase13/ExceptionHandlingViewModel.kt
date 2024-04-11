package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase13

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class ExceptionHandlingViewModel(
    private val api: MockApi = mockApi(),
) : BaseViewModel<UiState>() {
    fun handleExceptionWithTryCatch() {
        uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = api.getAndroidVersionFeatures(27)
                uiState.value = UiState.Success(listOf(result))
            } catch (_: Exception) {
                uiState.value = UiState.Error("Error occurred")
            }
        }
    }

    fun handleWithCoroutineExceptionHandler() {
        uiState.value = UiState.Loading
        viewModelScope.launch(
            CoroutineExceptionHandler { _, _ ->
                uiState.value = UiState.Error("Error Occurred!")
            },
        ) {
            val result = api.getAndroidVersionFeatures(28)
            uiState.value = UiState.Success(listOf(result))
        }
    }

    fun showResultsEvenIfChildCoroutineFails() {
        uiState.value = UiState.Loading
        viewModelScope.launch {
            supervisorScope {
                val versions =
                    try {
                        api.getRecentAndroidVersions()
                    } catch (e: Exception) {
                        uiState.value = UiState.Error("Error Occurred!")
                        return@supervisorScope
                    }
                val deferredList =
                    versions.map {
                        async { api.getAndroidVersionFeatures(it.apiLevel) }
                    }
                val result =
                    deferredList.mapNotNull {
                        try {
                            it.await()
                        } catch (e: Exception) {
                            Log.d("showResults", "exception caught")
                            null
                        }
                    }
                if (result.isEmpty()) {
                    uiState.value = UiState.Error("Error Occurred!")
                } else {
                    uiState.value = UiState.Success(result)
                }
            }
        }
    }
}
