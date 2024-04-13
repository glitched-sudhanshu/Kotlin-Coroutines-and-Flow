package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase2

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lukaslechner.coroutineusecasesonandroid.MainCoroutineScopeRule
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesAndroid10
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class Perform2SequentialNetworkRequestsViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineScopeRule()

    @Test
    fun perform2SequentialNetworkRequestSuccess() {
        val fakeSuccessApi = FakeSuccessApi()
        val viewModel = Perform2SequentialNetworkRequestsViewModel(fakeSuccessApi)
        val uiStates = mutableListOf<UiState>()
        viewModel.uiState().observeForever {
            if (it != null) uiStates.add(it)
        }
        viewModel.perform2SequentialNetworkRequest()
        assertEquals(
            listOf(UiState.Loading, UiState.Success(mockVersionFeaturesAndroid10)),
            uiStates,
        )
    }

    @Test
    fun `first api fails`() {
        val fakeSuccessApi = FakeFirstApiError()
        val viewModel = Perform2SequentialNetworkRequestsViewModel(fakeSuccessApi)
        val uiStates = mutableListOf<UiState>()
        viewModel.uiState().observeForever {
            if (it != null) uiStates.add(it)
        }
        viewModel.perform2SequentialNetworkRequest()
        assertEquals(listOf(UiState.Loading, UiState.Error("Something went wrong!")), uiStates)
    }

    @Test
    fun `second api fails`() {
        val fakeSuccessApi = FakeSecondApiError()
        val viewModel = Perform2SequentialNetworkRequestsViewModel(fakeSuccessApi)
        val uiStates = mutableListOf<UiState>()
        viewModel.uiState().observeForever {
            if (it != null) uiStates.add(it)
        }
        viewModel.perform2SequentialNetworkRequest()
        assertEquals(listOf(UiState.Loading, UiState.Error("Something went wrong!")), uiStates)
    }
}
