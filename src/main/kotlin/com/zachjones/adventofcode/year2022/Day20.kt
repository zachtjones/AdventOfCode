package com.zachjones.adventofcode.year2022

const val DECRYPTION_KEY: Long = 811589153

fun main() {
    val content = FileReader2022.readFile(day = 20, example = false)
    val initialNumbers = content.split('\n').map { it.toLong() }

    // observations
    // there are duplicates
    // we have to move the elements around, can wrap around the list multiple times
    val solutionPart1 = InputStructure(initialNumbers, times = 1)
    val answer = solutionPart1.solve()
    println("Part 1: $answer")


    val solutionPart2 = InputStructure(initialNumbers.map { it * DECRYPTION_KEY }, times = 10)
    val answerPart2 = solutionPart2.solve()
    println("Part 2: $answerPart2")

}

private class InputStructure(input: List<Long>, private val times: Int) {

    private val list = input.withIndex().map { Element(it.index, it.value) }.toMutableList()
    private val size = list.size

    fun solve(): Long {
        repeat(times) {
            for (i in 0 until size) {
                // find the element by the original index
                val (index, element) = list.withIndex().first { it.value.originalIndex == i }
                list.removeAt(index)
                // Size has decreased by one, as we removed a value, so do the math accordingly
                val newIndex = (index.toLong() + element.value).mod(size.toLong() - 1L).toInt()
                list.add(newIndex, element)
            }
        }
        val indexOfZero = list.indexOfFirst { it.value == 0L }
        return listOf(1000, 2000, 3000).sumOf { list[(indexOfZero + it).mod(size)].value }
    }

    private data class Element(val originalIndex: Int, val value: Long)
}
