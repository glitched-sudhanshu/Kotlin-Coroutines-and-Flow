package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase7

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lukaslechner.coroutineusecasesonandroid.MainCoroutineScopeRule
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesOreo
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesPie
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TimeoutAndRetryViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineScopeRule()

    @Test
    fun `successful api call`() =
        runTest {
            val sc = currentTime
            val fakeApi = FakeSuccessApi()
            val viewModel = TimeoutAndRetryViewModel(fakeApi)
            val uiStates = observeState(viewModel)
            viewModel.performNetworkRequest()
            advanceUntilIdle()
            assertEquals(
                listOf(
                    UiState.Loading,
                    UiState.Success(listOf(mockVersionFeaturesOreo, mockVersionFeaturesPie)),
                ),
                uiStates,
            )
            assertEquals(900, (currentTime - sc))
        }

    private fun observeState(viewModel: TimeoutAndRetryViewModel): List<UiState> {
        val uiStates = mutableListOf<UiState>()
        viewModel.uiState().observeForever {
            if (it != null) uiStates.add(it)
        }
        return uiStates
    }
}
