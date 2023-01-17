package com.zachjones.adventofcode.year2022

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Year2022Tests {

    // I want to run each parameter on a separate thread to speed up execution
    // since some of these take a while to get the answer

    @ParameterizedTest
    @MethodSource("arguments")
    @Execution(ExecutionMode.CONCURRENT)
    fun scenarios(challenge: BaseChallenge2022, expected: Solution) {
        val answerPart1 = challenge.solvePart1()
        val answerPart2 = challenge.solvePart2()
        Solution(answerPart1, answerPart2) shouldBe expected
        println("Challenge $challenge finished successfully")
    }


    data class Solution(val part1: String, val part2: String)

    private fun arguments(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(
                Day1(isExample = true), Solution("24000", "45000")
            ),
            Arguments.of(
                Day1(isExample = false), Solution("73211", "213958")
            )
        )
    }
}