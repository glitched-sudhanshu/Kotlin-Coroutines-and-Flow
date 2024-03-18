package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase11

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigInteger
import kotlin.system.measureTimeMillis

class CooperativeCancellationViewModel(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private var job: Job? = null

    fun performCalculation(factorialOf: Int) {
        uiState.value = UiState.Loading
        job =
            viewModelScope.launch {
                var result: BigInteger
                val computationDuration =
                    measureTimeMillis {
                        result = calculateFactorial(factorialOf)
                    }
                var resultString = ""
                val stingComputation =
                    measureTimeMillis {
                        resultString =
                            withContext(Dispatchers.Default + CoroutineName("String conversion")) {
                                result.toString()
                            }
                    }
                uiState.value = UiState.Success(resultString, computationDuration, stingComputation)
            }
    }

    private suspend fun calculateFactorial(number: Int) =
        withContext(Dispatchers.Default) {
            var factorial = BigInteger.ONE
            for (i in 1..number) {
                ensureActive()
                factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
            }
            factorial
        }

    fun cancelCalculation() {
        job?.cancel()
    }

    fun uiState(): LiveData<UiState> = uiState

    private val uiState: MutableLiveData<UiState> = MutableLiveData()
}
