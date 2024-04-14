package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase3

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lukaslechner.coroutineusecasesonandroid.MainCoroutineScopeRule
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesAndroid10
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesOreo
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesPie
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class PerformNetworkRequestsConcurrentlyViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineScopeRule()

    @Test
    fun `should call apis concurrently via runBlockingTest`() =
        mainCoroutineScopeRule.runBlockingTest {
            val startTime = System.currentTimeMillis()
            val startCurrent = currentTime
            val fakeSuccessApi = FakeSuccessApi()
            val viewModel = PerformNetworkRequestsConcurrentlyViewModel(fakeSuccessApi)
            val uiStates = observeState(viewModel)
            viewModel.performNetworkRequestsConcurrently()
            advanceUntilIdle()
            assertEquals(
                listOf(
                    UiState.Loading,
                    UiState.Success(
                        listOf(
                            mockVersionFeaturesOreo,
                            mockVersionFeaturesPie,
                            mockVersionFeaturesAndroid10,
                        ),
                    ),
                ),
                uiStates,
            )
            assertEquals(1000, (currentTime - startCurrent))
            println("time: ${System.currentTimeMillis() - startTime}  \n\n virtual time: ${currentTime - startCurrent}")
        }

//    runTest worked w/o using extension function
    @Test
    fun `should call apis sequentially via runTest`() =
        runTest {
            val startTime = System.currentTimeMillis()
            val startCurrent = currentTime
            val fakeSuccessApi = FakeSuccessApi()
            val viewModel = PerformNetworkRequestsConcurrentlyViewModel(fakeSuccessApi)
            val uiStates = mutableListOf<UiState>()
            viewModel.uiState().observeForever {
                if (it != null) uiStates.add(it)
            }
            viewModel.performNetworkRequestsSequentially()
            advanceUntilIdle()
            assertEquals(
                listOf(
                    UiState.Loading,
                    UiState.Success(
                        listOf(
                            mockVersionFeaturesOreo,
                            mockVersionFeaturesPie,
                            mockVersionFeaturesAndroid10,
                        ),
                    ),
                ),
                uiStates,
            )
            assertEquals(3000, (currentTime - startCurrent))
            println("time: ${System.currentTimeMillis() - startTime}  \n\n virtual time: ${currentTime - startCurrent}")
        }

    private fun observeState(viewModel: PerformNetworkRequestsConcurrentlyViewModel): List<UiState> {
        val uiStates = mutableListOf<UiState>()
        viewModel.uiState().observeForever {
            if (it != null) uiStates.add(it)
        }
        return uiStates
    }
}
