package com.zachjones.adventofcode.year2022

class Day3(isExample: Boolean): BaseChallenge2022(day = 3, isExample) {
    private val rucksacks = inputContent.split('\n')
    override fun solvePart1(): String {
        val totalPriority = rucksacks.map { sack ->
            val split = sack.chunked(sack.length / 2)
            val left = split[0].toCharArray().toSet()
            val right = split[1].toCharArray().toSet()
            val common = left.intersect(right)
            require(common.size == 1) {
                "Invalid rucksack:$sack; should be 1 letter in common between halves"
            }
            return@map common.first().priority()
        }.sum()
        return totalPriority.toString()
    }

    override fun solvePart2(): String {
        val totalPriorityPart2 = rucksacks.chunked(3).map { group ->
            val one = group[0].toCharArray().toSet()
            val two = group[1].toCharArray().toSet()
            val three = group[2].toCharArray().toSet()
            val badge = one.intersect(two).intersect(three)
            require(badge.size == 1) {
                "Invalid rucksack group:$group; should be 1 letter in common between 3 groups"
            }
            return@map badge.first().priority()
        }.sum()

        return totalPriorityPart2.toString()
    }

}

private fun Char.priority(): Int = when(this) {
    in 'a'..'z' -> this.minus('a') + 1
    in 'A'..'Z' -> this.minus('A') + 27
    else -> throw IllegalArgumentException("Unexpected character: $this")
}

fun main() {
    val day = Day3(isExample = false)
    println("Part 1: ${day.solvePart1()}")
    println("Part 2: ${day.solvePart2()}")
}