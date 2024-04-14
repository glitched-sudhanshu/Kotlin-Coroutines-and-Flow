package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase5

import com.lukaslechner.coroutineusecasesonandroid.mock.AndroidVersion
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import com.lukaslechner.coroutineusecasesonandroid.mock.VersionFeatures
import com.lukaslechner.coroutineusecasesonandroid.mock.mockAndroidVersions
import kotlinx.coroutines.delay

class FakeApi(private val responseDelay: Long = 1000L) : MockApi {
    override suspend fun getRecentAndroidVersions(): List<AndroidVersion> {
        delay(responseDelay)
        if (responseDelay > 1500L) throw IllegalArgumentException()
        return mockAndroidVersions
    }

    override suspend fun getAndroidVersionFeatures(apiLevel: Int): VersionFeatures {
        delay(responseDelay)
        throw IllegalArgumentException()
    }
}
