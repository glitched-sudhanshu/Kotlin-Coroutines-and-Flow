package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase4

import com.lukaslechner.coroutineusecasesonandroid.mock.AndroidVersion
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import com.lukaslechner.coroutineusecasesonandroid.mock.VersionFeatures
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesAndroid10
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesOreo
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesPie
import kotlinx.coroutines.delay

class FakeFirstApiError : MockApi {
    override suspend fun getRecentAndroidVersions(): List<AndroidVersion> {
        throw IllegalArgumentException()
    }

    override suspend fun getAndroidVersionFeatures(apiLevel: Int): VersionFeatures {
        delay(1000)
        return when (apiLevel) {
            27 -> mockVersionFeaturesOreo
            28 -> mockVersionFeaturesPie
            29 -> mockVersionFeaturesAndroid10
            else -> throw IllegalArgumentException()
        }
    }
}
