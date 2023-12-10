package com.zachjones.adventofcode.year2023

import com.zachjones.adventofcode.BaseChallenge

abstract class BaseChallenge2023(
    private val day: Int,
    private val isExample: Boolean,
    private val separatePart2: Boolean
) : BaseChallenge(day, isExample) {

    /**
     * The loaded input's content, based on the day and example/not.
     * This will contain the '\n' characters for newlines
     */
    final override val inputContent: String = FileReader2023.readFile(day, isExample)

    override fun solvePart1(): String = solvePart1(inputContent)
    override fun solvePart2(): String =
        solvePart2(if (separatePart2) FileReader2023.readFile(day, isExample, true) else inputContent)

    /**
     * Solves the puzzle for part 1, given the input
     */
    abstract fun solvePart1(input: String): String

    /**
     * Solves the puzzle for part 2, given the input
     */
    abstract fun solvePart2(input: String): String
}
