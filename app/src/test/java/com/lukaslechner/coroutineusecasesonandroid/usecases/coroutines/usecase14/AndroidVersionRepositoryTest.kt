package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase14

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lukaslechner.coroutineusecasesonandroid.MainCoroutineScopeRule
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class AndroidVersionRepositoryTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineScopeRule = MainCoroutineScopeRule()

    @Test
    fun `fun loadAndStoreRemoteAndroidVersions() should work when coroutine gets cancelled`() =
        runTest {
            val fakeDatabase = FakeDatabase()
            val fakeApi = FakeApi()
            val sut = AndroidVersionRepository(fakeDatabase, mainCoroutineScopeRule, fakeApi)
            val mockViewModelCoroutineScope = TestCoroutineScope()
            mockViewModelCoroutineScope.launch {
                sut.loadAndStoreRemoteAndroidVersions()
                fail("Scope should be cancelled before versions are loaded")
            }
            mockViewModelCoroutineScope.cancel()
            // to call the child suspend functions, as they are not executed by default.
            advanceUntilIdle()
            assertEquals(true, fakeDatabase.insertIntoDb)
        }

    suspend fun test(): Int {
        delay(30)
        return 42
    }

    @Test
    fun testFun() =
        runTest {
            val sc = currentTime
            launch {
                test()
            }
            advanceUntilIdle()
            assertEquals(30, (currentTime - sc))
        }

    @Test
    fun testFunNegation() =
        runTest {
            val sc = currentTime
            launch {
                test()
            }
            assertEquals(0, (currentTime - sc))
        }
}
