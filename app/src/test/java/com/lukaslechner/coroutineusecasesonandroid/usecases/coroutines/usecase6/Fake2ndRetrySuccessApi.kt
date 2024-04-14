package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase6

import com.lukaslechner.coroutineusecasesonandroid.mock.AndroidVersion
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import com.lukaslechner.coroutineusecasesonandroid.mock.VersionFeatures
import com.lukaslechner.coroutineusecasesonandroid.mock.mockAndroidVersions
import kotlinx.coroutines.delay

class Fake2ndRetrySuccessApi(private val responseDelay: Long = 1000L) : MockApi {
    var requestCount = 0

    override suspend fun getRecentAndroidVersions(): List<AndroidVersion> {
        requestCount++
        delay(responseDelay)
        if (requestCount > 2) {
            return mockAndroidVersions
        }
        throw IllegalArgumentException()
    }

    override suspend fun getAndroidVersionFeatures(apiLevel: Int): VersionFeatures {
        throw IllegalArgumentException()
    }
}
