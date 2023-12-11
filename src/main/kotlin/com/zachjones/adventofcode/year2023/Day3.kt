package com.zachjones.adventofcode.year2023

import com.zachjones.adventofcode.product

class Day3(isExample: Boolean) : BaseChallenge2023(day = 3, isExample, separatePart2 = false) {

    private val symbols = setOf('!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '+', '-', '/', '=')
    private val numberRegex = Regex("\\d+")
    override fun solvePart1(input: String): String {
        val lines = input.split('\n')
        val partNumbers = mutableListOf<Long>()
        lines.forEachIndexed { index, line ->
            val matches = numberRegex.findAll(line).toList()
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
        val lines = input.split('\n')
        val numbersMatches: List<Pair<Int, MatchResult>> = lines.flatMapIndexed { index, line ->
            val matches = numberRegex.findAll(line).toList()
            matches.map { match ->
                index to match
            }
        }
        val potentialGears: List<Pair<Int, Int>> = lines.flatMapIndexed { index, line ->
            val matches = Regex("\\*").findAll(line).toList()
            matches.map { match ->
                index to match.range.first
            }
        }
        return potentialGears.sumOf {
            val numbers = numbersAdjacentTo(gear = it, numbersMatches)
            if (numbers.size == 2) {
                numbers.product().toLong()
            } else {
                0L
            }
        }.toString()
    }

    private fun numbersAdjacentTo(gear: Pair<Int, Int>, numberMatches: List<Pair<Int, MatchResult>>): List<Int> {
        val surroundingPoints = listOf(
            gear.first - 1 to gear.second,
            gear.first + 1 to gear.second,
            gear.first to gear.second - 1,
            gear.first to gear.second + 1,
            gear.first - 1 to gear.second - 1,
            gear.first + 1 to gear.second + 1,
            gear.first - 1 to gear.second + 1,
            gear.first + 1 to gear.second - 1
        )
        return numberMatches.filter { numberMatch ->
            surroundingPoints.any { point ->
                numberMatch.first == point.first && point.second in numberMatch.second.range
            }
        }.map { it.second.value.toInt() }
    }
}
