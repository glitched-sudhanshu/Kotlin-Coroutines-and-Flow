package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase12

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.math.BigInteger

class FactorialCalculator(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    suspend fun calculateFactorial(
        factorialOf: Int,
        numberOfCoroutines: Int,
    ) = withContext(defaultDispatcher) {
        val subRange = createSubRangeList(factorialOf, numberOfCoroutines)
        subRange.map {
            async { calculateFactorialOfSubRange(it) }
        }.awaitAll()
            .fold(BigInteger.ONE) { acc, ele ->
                acc.multiply(ele)
            }
    }

    fun calculateFactorialOfSubRange(subRange: SubRange): BigInteger {
        var factorial = BigInteger.ONE
        for (i in subRange.start..subRange.end) {
            factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
        }
        return factorial
    }

    fun createSubRangeList(
        factorialOf: Int,
        numberOfSubRanges: Int,
    ): List<SubRange> {
        val quotient = factorialOf.div(numberOfSubRanges)
        val rangesList = mutableListOf<SubRange>()

        var curStartIndex = 1
        repeat(numberOfSubRanges - 1) {
            rangesList.add(
                SubRange(
                    curStartIndex,
                    curStartIndex + (quotient - 1),
                ),
            )
            curStartIndex += quotient
        }
        rangesList.add(SubRange(curStartIndex, factorialOf))
        return rangesList
    }
}

data class SubRange(val start: Int, val end: Int)
