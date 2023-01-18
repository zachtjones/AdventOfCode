package com.zachjones.adventofcode.year2022

class Day1(isExample: Boolean): BaseChallenge2022(day = 1, isExample) {

    private val elfBundles: List<Int> = this.inputContent
            // each elf is split by two empty lines
        .split(Regex("\n\n"))
        // calculate their sum
        .map { bundle -> bundle.split('\n').sumOf { it.toInt() } }

    override fun solvePart1(): String {
        return elfBundles.max().toString()
    }

    override fun solvePart2(): String {
        val top3 = elfBundles.sortedDescending().take(3)
        return top3.sum().toString()
    }

}

fun main() {
    val day = Day1(isExample = false)
    println("Part 1: ${day.solvePart1()}")
    println("Part 2: ${day.solvePart2()}")
}