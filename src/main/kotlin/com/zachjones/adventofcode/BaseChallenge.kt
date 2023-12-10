package com.zachjones.adventofcode

abstract class BaseChallenge(private val day: Int, private val isExample: Boolean) {

    /**
     * The loaded input's content, based on the day and example/not.
     * This will contain the '\n' characters for newlines
     */
    abstract val inputContent: String

    /**
     * Solves the puzzle for part 1, given the input
     */
    abstract fun solvePart1(): String

    /**
     * Solves the puzzle for part 2, given the input
     */
    abstract fun solvePart2(): String

    override fun toString() = "Day=$day, isExample=$isExample"
}
