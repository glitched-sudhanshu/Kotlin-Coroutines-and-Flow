package com.lukaslechner.coroutineusecasesonandroid

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class MainCoroutineScopeRule(private val testCoroutineDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()) :
    TestWatcher() {
    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        testCoroutineDispatcher.cleanupTestCoroutines()
        Dispatchers.resetMain()
    }
}
