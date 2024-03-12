package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase10

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigInteger
import kotlin.system.measureTimeMillis

class CalculationInBackgroundViewModel : BaseViewModel<UiState>() {

    fun performCalculation(factorialOf: Int) {
        uiState.value = UiState.Loading
        viewModelScope.launch {
            var result: BigInteger
            val computationDuration = measureTimeMillis {
                result = calculateFactorial(factorialOf)
            }
            var resultString = ""
            val stingComputation = measureTimeMillis {
                resultString =
                    withContext(Dispatchers.Default + CoroutineName("String conversion"))
                    {
                        result.toString()
                    }
            }
            uiState.value = UiState.Success(resultString, computationDuration, stingComputation)
        }
    }

    private suspend fun calculateFactorial(number: Int) = withContext(Dispatchers.Default) {
        var factorial = BigInteger.ONE
        for (i in 1..number) {
            factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
        }
        factorial
    }

}