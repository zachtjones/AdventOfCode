package com.zachjones.adventofcode.year2023

import com.zachjones.adventofcode.Solution
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Year2023Tests {

    // I want to run each parameter on a separate thread to speed up execution
    // since some of these take a while to get the answer

    // @Execution(ExecutionMode.CONCURRENT)
    @ParameterizedTest
    @MethodSource("arguments")
    fun scenarios(challenge: BaseChallenge2023, expected: Solution) {
        val answerPart1 = challenge.solvePart1()
        val answerPart2 = challenge.solvePart2()
        Solution(answerPart1, answerPart2) shouldBe expected
        println("Challenge $challenge finished successfully")
    }

    private fun arguments(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(
                Day1(isExample = true),
                Solution("142", "281"),
            ),
            Arguments.of(
                Day1(isExample = false),
                Solution("55172", ""), // TODO - there is a problem here
            ),
            Arguments.of(
                Day2(isExample = true),
                Solution("8", "2286"),
            ),
            Arguments.of(
                Day2(isExample = false),
                Solution("2505", "70265"),
            ),
            Arguments.of(
                Day3(isExample = true),
                Solution("4361", ""),
            ),
            Arguments.of(
                Day3(isExample = false),
                Solution("527369", ""),
            ),
        )
    }
}
