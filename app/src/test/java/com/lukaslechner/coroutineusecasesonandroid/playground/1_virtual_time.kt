package com.lukaslechner.coroutineusecasesonandroid.playground

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SystemUnderTest {
    suspend fun doAfterDelay(): Int {
        delay(1000)
        return 49
    }
}

fun CoroutineScope.nestedCall() =
    launch {
        delay(1000)
        println("Done")
    }

class TestClass {
    @Test
    fun `test doAfterDelay using runBlocking`() =
        runBlockingTest {
            val sut = SystemUnderTest()
            val result = sut.doAfterDelay()
            assertEquals(49, result)
        }

    // runBlockingTest only auto adnvances delay calls that are called directly from the runBlockingTest coroutine scope. For any child delay calls it gives us fine grained control over the execution of the delay fun. Whether we want to skip it or not.
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test nestedCall using runBlocking`() =
        runBlockingTest {
            val sReal = System.currentTimeMillis()
            val sVir = currentTime
            nestedCall()

            // this method will skip the child delay
            advanceUntilIdle()
            println("real: ${System.currentTimeMillis() - sReal} \n vir: ${currentTime - sVir}")
        }

    @Test
    fun `test nestedCall using runTest`() =
        runTest {
            val sReal = System.currentTimeMillis()
            val sVir = currentTime
            nestedCall()
            advanceUntilIdle()
            println("real: ${System.currentTimeMillis() - sReal} \n vir: ${currentTime - sVir}")
        }
}
