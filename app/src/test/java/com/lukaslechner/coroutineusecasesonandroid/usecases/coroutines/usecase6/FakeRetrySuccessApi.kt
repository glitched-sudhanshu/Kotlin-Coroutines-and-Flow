package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase6

import com.lukaslechner.coroutineusecasesonandroid.mock.AndroidVersion
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import com.lukaslechner.coroutineusecasesonandroid.mock.VersionFeatures
import com.lukaslechner.coroutineusecasesonandroid.mock.mockAndroidVersions
import kotlinx.coroutines.delay

class FakeRetrySuccessApi : MockApi {
    var requestCount = 0

    override suspend fun getRecentAndroidVersions(): List<AndroidVersion> {
        requestCount++
        delay(1000L)
        if (requestCount > 1) {
            return mockAndroidVersions
        }
        throw IllegalArgumentException()
    }

    override suspend fun getAndroidVersionFeatures(apiLevel: Int): VersionFeatures {
        throw IllegalArgumentException()
    }
}
