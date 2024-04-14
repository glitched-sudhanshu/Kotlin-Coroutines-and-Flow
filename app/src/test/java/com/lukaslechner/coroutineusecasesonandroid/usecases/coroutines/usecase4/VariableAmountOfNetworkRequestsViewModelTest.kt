package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase4

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lukaslechner.coroutineusecasesonandroid.MainCoroutineScopeRule
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesAndroid10
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesOreo
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesPie
import junit.framework.TestCase
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class VariableAmountOfNetworkRequestsViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineScopeRule()

    @Test
    fun `should call variable no of api concurrently success`() =
        runTest {
            val startCurrent = currentTime
            val fakeSuccessApi = FakeSuccessApi()
            val viewModel = VariableAmountOfNetworkRequestsViewModel(fakeSuccessApi)
            val uiStates = observeState(viewModel)
            viewModel.performNetworkRequestsConcurrently()
            advanceUntilIdle()
            TestCase.assertEquals(
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
            TestCase.assertEquals(1000, (currentTime - startCurrent))
        }

    @Test
    fun `concurrent api call 2nd api fails`() =
        runTest {
            val startCurrent = currentTime
            val fakeSecondApiError = FakeSecondApiError()
            val viewModel = VariableAmountOfNetworkRequestsViewModel(fakeSecondApiError)
            val uiStates = observeState(viewModel)
            viewModel.performNetworkRequestsConcurrently()
            advanceUntilIdle()
            TestCase.assertEquals(
                listOf(
                    UiState.Loading,
                    UiState.Error("Something went wrong!"),
                ),
                uiStates,
            )
            TestCase.assertEquals(1000, (currentTime - startCurrent))
        }

    @Test
    fun `should call variable no of api sequentially`() =
        runTest {
            val startCurrent = currentTime
            val fakeSuccessApi = FakeSuccessApi()
            val viewModel = VariableAmountOfNetworkRequestsViewModel(fakeSuccessApi)
            val uiStates = observeState(viewModel)
            viewModel.performNetworkRequestsSequentially()
            advanceUntilIdle()
            TestCase.assertEquals(
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
            TestCase.assertEquals(3000, (currentTime - startCurrent))
        }

    @Test
    fun `sequential api call 1st api fails`() =
        runTest {
            val startCurrent = currentTime
            val fakeFirstApiError = FakeFirstApiError()
            val viewModel = VariableAmountOfNetworkRequestsViewModel(fakeFirstApiError)
            val uiStates = observeState(viewModel)
            viewModel.performNetworkRequestsSequentially()
            advanceUntilIdle()
            TestCase.assertEquals(
                listOf(
                    UiState.Loading,
                    UiState.Error("Something went wrong!"),
                ),
                uiStates,
            )
            TestCase.assertEquals(0, (currentTime - startCurrent))
        }

    private fun observeState(viewModel: VariableAmountOfNetworkRequestsViewModel): List<UiState> {
        val uiStates = mutableListOf<UiState>()
        viewModel.uiState().observeForever {
            if (it != null) uiStates.add(it)
        }
        return uiStates
    }
}
