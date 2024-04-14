package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase5

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lukaslechner.coroutineusecasesonandroid.MainCoroutineScopeRule
import com.lukaslechner.coroutineusecasesonandroid.mock.mockAndroidVersions
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class NetworkRequestWithTimeoutViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineScopeRule()

    @Test
    fun `api call time out failure`() =
        runTest {
            val sc = currentTime
            val fakeApi = FakeApi(1000L)
            val viewModel = NetworkRequestWithTimeoutViewModel(fakeApi)
            val uiStates = observeState(viewModel)
            viewModel.performNetworkRequest(500L)
            advanceUntilIdle()
            assertEquals(listOf(UiState.Loading, UiState.Error("Network timeout!")), uiStates)
            assertEquals(1000L, (currentTime - sc))
        }

    @Test
    fun `api call network failure`() =
        runTest {
            val sc = currentTime
            val fakeApi = FakeApi(2000L)
            val viewModel = NetworkRequestWithTimeoutViewModel(fakeApi)
            val uiStates = observeState(viewModel)
            viewModel.performNetworkRequest(2500L)
            advanceUntilIdle()
            assertEquals(listOf(UiState.Loading, UiState.Error("Something went wrong!")), uiStates)
            assertEquals(2000L, (currentTime - sc))
        }

    @Test
    fun `api call success`() =
        runTest {
            val sc = currentTime
            val fakeApi = FakeApi(1000L)
            val viewModel = NetworkRequestWithTimeoutViewModel(fakeApi)
            val uiStates = observeState(viewModel)
            viewModel.performNetworkRequest(1500L)
            advanceUntilIdle()
            assertEquals(listOf(UiState.Loading, UiState.Success(mockAndroidVersions)), uiStates)
            assertEquals(1000L, (currentTime - sc))
        }

    private fun observeState(viewModel: NetworkRequestWithTimeoutViewModel): List<UiState> {
        val uiStates = mutableListOf<UiState>()
        viewModel.uiState().observeForever {
            if (it != null) uiStates.add(it)
        }
        return uiStates
    }
}
