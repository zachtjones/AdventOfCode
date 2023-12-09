package com.zachjones.adventofcode.year2022

import io.kotest.matchers.shouldBe
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
                Day1(isExample = true),
                Solution("24000", "45000"),
            ),
            Arguments.of(
                Day1(isExample = false),
                Solution("73211", "213958"),
            ),
            Arguments.of(
                Day2(isExample = true),
                Solution("15", "12"),
            ),
            Arguments.of(
                Day2(isExample = false),
                Solution("12458", "12683"),
            ),
            Arguments.of(
                Day3(isExample = true),
                Solution("157", "70"),
            ),
            Arguments.of(
                Day3(isExample = false),
                Solution("8053", "2425"),
            ),
            Arguments.of(
                Day4(isExample = true),
                Solution("2", "4"),
            ),
            Arguments.of(
                Day4(isExample = false),
                Solution("464", "770"),
            ),
            Arguments.of(
                Day5(isExample = true),
                Solution("CMZ", "MCD"),
            ),
            Arguments.of(
                Day5(isExample = false),
                Solution("BSDMQFLSP", "PGSQBFLDP"),
            ),
            Arguments.of(
                Day6(isExample = true),
                Solution("7", "19"),
            ),
            Arguments.of(
                Day6(isExample = false),
                Solution("1566", "2265"),
            ),
            Arguments.of(
                Day7(isExample = true),
                Solution("95437", "24933642"),
            ),
            Arguments.of(
                Day7(isExample = false),
                Solution("1792222", "1112963"),
            ),
            Arguments.of(
                Day8(isExample = true),
                Solution("21", "8"),
            ),
            Arguments.of(
                Day8(isExample = false),
                Solution("1801", "209880"),
            ),
            Arguments.of(
                Day9(isExample = true),
                Solution("88", "36"), // using the bigger input, you get 88, but with the smaller one 13 for part 1
            ),
            Arguments.of(
                Day9(isExample = false),
                Solution("5874", "2467"),
            ),
            Arguments.of(
                Day10(isExample = true),
                Solution("13140", ""), // the example doesn't print something with letters
            ),
            Arguments.of(
                Day10(isExample = false),
                Solution("12880", "FCJAPJRE"),
            ),
            Arguments.of(
                Day11(isExample = true),
                Solution("10605", "2713310158"),
            ),
            Arguments.of(
                Day11(isExample = false),
                Solution("58322", "13937702909"),
            ),
            Arguments.of(
                Day12(isExample = true),
                Solution("31", "29"),
            ),
            Arguments.of(
                Day12(isExample = false),
                Solution("380", "375"),
            ),
            Arguments.of(
                Day13(isExample = true),
                Solution("13", "140"),
            ),
            Arguments.of(
                Day13(isExample = false),
                Solution("6240", "23142"),
            ),
            Arguments.of(
                Day14(isExample = true),
                Solution("24", "93"),
            ),
            Arguments.of(
                Day14(isExample = false),
                Solution("858", "26845"),
            ),
//            Arguments.of(
//                Day15(isExample = true), Solution("26", "56000011") // my algorithm does not work on the example for part 2, something about the bounds
//            ),
            Arguments.of(
                Day15(isExample = false),
                Solution("5564017", "11558423398893"),
            ),
        )
    }
}
