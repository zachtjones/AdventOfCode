package com.zachjones.adventofcode.year2022

class Day6(isExample: Boolean): BaseChallenge2022(day = 6, isExample) {
    override fun solvePart1(): String {
        val slices = inputContent.windowed(size = 4, step = 1)
        val solution = findSolution(slices) + 4 // add the size on, since that's the repeated length
        return solution.toString()
    }

    override fun solvePart2(): String {
        val slicesPart2 = inputContent.windowed(size = 14, step = 1)
        val solutionPart2 = findSolution(slicesPart2) + 14
        return solutionPart2.toString()
    }

}
fun main() {
    val day = Day6(isExample = false)
    println("Part 1: ${day.solvePart1()}")
    println("Part 2: ${day.solvePart2()}")
}

private fun findSolution(slices: List<String>): Int {
    for (i in slices.indices) {
        if (slices[i].isAllDifferent()) {
            return i
        }
    }
    throw IllegalArgumentException("No match found")
}

private fun String.isAllDifferent(): Boolean =
    this.toCharArray().toSet().size == this.length