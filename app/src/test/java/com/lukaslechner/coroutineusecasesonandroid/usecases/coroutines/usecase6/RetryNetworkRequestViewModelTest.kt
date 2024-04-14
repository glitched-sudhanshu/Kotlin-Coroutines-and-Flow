package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase6

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lukaslechner.coroutineusecasesonandroid.MainCoroutineScopeRule
import com.lukaslechner.coroutineusecasesonandroid.mock.mockAndroidVersions
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class RetryNetworkRequestViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineScopeRule()

    @Test
    fun `retry api call 2 times then success`() =
        runTest {
            val sc = currentTime
            val fakeApi = Fake2ndRetrySuccessApi()
            val viewModel = RetryNetworkRequestViewModel(fakeApi)
            val uiStates = observeState(viewModel)
            viewModel.performNetworkRequest()
            advanceUntilIdle()
            assertEquals(listOf(UiState.Loading, UiState.Success(mockAndroidVersions)), uiStates)
            assertEquals(3, fakeApi.requestCount)
            // 3*1000L per api call; 100L first retry, 200L second retry
            assertEquals(3300, (currentTime - sc))
        }

    @Test
    fun `retry api call 1 time then success`() =
        runTest {
            val sc = currentTime
            val fakeApi = FakeRetrySuccessApi()
            val viewModel = RetryNetworkRequestViewModel(fakeApi)
            val uiStates = observeState(viewModel)
            viewModel.performNetworkRequest()
            advanceUntilIdle()
            assertEquals(listOf(UiState.Loading, UiState.Success(mockAndroidVersions)), uiStates)
            assertEquals(2, fakeApi.requestCount)
            assertEquals(2100, (currentTime - sc))
        }

    @Test
    fun `api fails every time`() =
        runTest {
            val sc = currentTime
            val fakeApi = FakeFailureApi()
            val viewModel = RetryNetworkRequestViewModel(fakeApi)
            val uiStates = observeState(viewModel)
            viewModel.performNetworkRequest()
            advanceUntilIdle()
            assertEquals(listOf(UiState.Loading, UiState.Error("Something went wrong!")), uiStates)
            assertEquals(3, fakeApi.requestCount)
            assertEquals(3300, (currentTime - sc))
        }

    private fun observeState(viewModel: RetryNetworkRequestViewModel): List<UiState> {
        val uiStates = mutableListOf<UiState>()
        viewModel.uiState().observeForever {
            if (it != null) uiStates.add(it)
        }
        return uiStates
    }
}
