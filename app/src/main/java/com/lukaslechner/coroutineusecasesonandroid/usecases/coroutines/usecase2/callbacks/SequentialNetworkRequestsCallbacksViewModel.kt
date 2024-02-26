package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase2.callbacks

import android.os.Looper
import android.util.Log
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.AndroidVersion
import com.lukaslechner.coroutineusecasesonandroid.mock.VersionFeatures
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class SequentialNetworkRequestsCallbacksViewModel(
    private val mockApi: CallbackMockApi = mockApi()
) : BaseViewModel<UiState>() {

    private var androidVersionListCall : Call<List<AndroidVersion>>? = null
    private var androidFeatureCall : Call<VersionFeatures>? = null

    fun perform2SequentialNetworkRequest() {
        uiState.value = UiState.Loading

        androidVersionListCall = mockApi.getRecentAndroidVersions()
        androidVersionListCall!!.enqueue(object : Callback<List<AndroidVersion>> {
            override fun onResponse(
                call: Call<List<AndroidVersion>>,
                response: Response<List<AndroidVersion>>
            ) {
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    Log.d("Thread Info", "Executing on the Main/UI Thread")
                }
                if(response.isSuccessful){
                    val latestAndroid = response.body()!!.last().apiLevel
                    androidFeatureCall = mockApi.getAndroidVersionFeatures(latestAndroid)
                    androidFeatureCall!!.enqueue(object : Callback<VersionFeatures>{
                        override fun onResponse(
                            call: Call<VersionFeatures>,
                            response: Response<VersionFeatures>
                        ) {
                            if(response.isSuccessful){
                                uiState.value = UiState.Success(response.body()!!)
                            }else uiState.value = UiState.Error("Something went wrong!")
                        }

                        override fun onFailure(call: Call<VersionFeatures>, t: Throwable) {
                            uiState.value = UiState.Error("Something went wrong!")
                        }
                    })
                }else uiState.value = UiState.Error("Something went wrong!")
            }

            override fun onFailure(call: Call<List<AndroidVersion>>, t: Throwable) {
                uiState.value = UiState.Error("Something went wrong!")
            }
        })
    }

    //this alone can cause memory leaks, as viewmodel and activity are not garbage collected
    //coz callbacks still hold a reference to the viewmodel.
    override fun onCleared() {
        super.onCleared()
        androidVersionListCall?.cancel()
        androidFeatureCall?.cancel()
    }
}