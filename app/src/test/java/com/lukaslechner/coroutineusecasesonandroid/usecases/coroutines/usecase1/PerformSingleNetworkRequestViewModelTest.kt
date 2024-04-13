package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase1

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lukaslechner.coroutineusecasesonandroid.MainCoroutineScopeRule
import com.lukaslechner.coroutineusecasesonandroid.mock.mockAndroidVersions
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class PerformSingleNetworkRequestViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineScopeRule()

    @Test
    fun performSingleNetworkRequest() {
        val fakeMockApi = FakeMockApi()
        val viewModel = PerformSingleNetworkRequestViewModel(fakeMockApi)
        val uiStates = mutableListOf<UiState>()
        viewModel.uiState().observeForever {
            if (it != null) {
                uiStates.add(it)
            }
        }
        viewModel.performSingleNetworkRequest()
        assertEquals(listOf(UiState.Loading, UiState.Success(mockAndroidVersions)), uiStates)
    }

    @Test
    fun `return error when network fails`() {
        val fakeErrorApi = FakeErrorApi()
        val viewModel = PerformSingleNetworkRequestViewModel(fakeErrorApi)
        val uiStates = mutableListOf<UiState>()
        viewModel.uiState().observeForever {
            if (it != null) {
                uiStates.add(it)
            }
        }
        viewModel.performSingleNetworkRequest()
        assertEquals(listOf(UiState.Loading, UiState.Error("Something went wrong!")), uiStates)
    }
}
