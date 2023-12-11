package com.zachjones.adventofcode.year2023

class Day3(isExample: Boolean) : BaseChallenge2023(day = 3, isExample, separatePart2 = false) {

    private val symbols = setOf('!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '+', '-', '/', '=')
    private val regex = Regex("\\d+")
    override fun solvePart1(input: String): String {
        val lines = input.split('\n')
        val partNumbers = mutableListOf<Long>()
        lines.forEachIndexed { index, line ->
            val matches = regex.findAll(line).toList()
            matches.forEach { match ->
                if (isSymbolSurrounding(input = lines, lineNumber = index, columns = match.range)) {
                    partNumbers += match.value.toLong()
                }
            }
        }
        return partNumbers.sum().toString()
    }

    private fun isSymbolSurrounding(input: List<String>, lineNumber: Int, columns: IntRange): Boolean {
        val range = IntRange(columns.first - 1, columns.last + 1)
        // one row up, current row, or down
        return range.any {
            isSymbolAt(input, lineNumber - 1, it) ||
                isSymbolAt(input, lineNumber, it) ||
                isSymbolAt(input, lineNumber + 1, it)
        }
    }

    private fun isSymbolAt(input: List<String>, lineNumber: Int, columnNumber: Int): Boolean {
        if (lineNumber < 0 || columnNumber < 0 || lineNumber >= input.size || columnNumber >= input[0].length) {
            return false
        }
        return (input[lineNumber][columnNumber] in symbols)
    }

    override fun solvePart2(input: String): String {
        return ""
    }
}
