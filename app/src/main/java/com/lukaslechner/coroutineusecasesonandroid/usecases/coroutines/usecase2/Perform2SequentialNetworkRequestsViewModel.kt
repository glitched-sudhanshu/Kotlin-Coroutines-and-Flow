package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase2

import android.os.Looper
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.launch

class Perform2SequentialNetworkRequestsViewModel(
    private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun perform2SequentialNetworkRequest() {

        uiState.value = UiState.Loading

        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.d("Thread Info", "Executing on the Main/UI Thread")
        }else
            Log.d("Thread Info", "Executing NOT Main/UI Thread")

        //viewModelScope is the coroutine scope, we need a coroutine scope to start a coroutine.
        //it holds the various property of that coroutine, like when they should be cancelled, the thread they should be running on.
        //launch is Coroutine Builder
        viewModelScope.launch{

            if (Looper.myLooper() == Looper.getMainLooper()) {
                Log.d("Thread Info", "Main/UI Thread")
            }else
                Log.d("Thread Info", "NOT Main/UI Thread")

            //this code has been executed on the main thread.
            //suspend fun of retrofit are main safe, which means they can safely run on main thread without blocking it
            //retrofit itself takes care of performing actual network request on the background thread
            try {
                val listResult = mockApi.getRecentAndroidVersions().last()
                val features = mockApi.getAndroidVersionFeatures(listResult.apiLevel)
                uiState.value = UiState.Success(features)
            } catch (e: Exception) {
                uiState.value = UiState.Error("Something went wrong!")
            }
        }

    }
}