package com.zachjones.adventofcode.year2023

class Day1(isExample: Boolean) : BaseChallenge2023(day = 1, isExample, separatePart2 = true) {

    override fun solvePart1(input: String): String {
        val lines = input.split('\n')
        return lines.sumOf {
            // first digit in the string concatenated with the last digit
            it.first { char -> char.isDigit() }.digitToInt() * 10 + it.last { char -> char.isDigit() }.digitToInt()
        }.toString()
    }

    override fun solvePart2(input: String): String {
        val lines = input.split('\n')
        return lines.sumOf {
            it.firstDigit() * 10 + it.lastDigit()
        }.toString()
    }

    private val digitRegex = Regex("(one)|(two)|(three)|(four)|(five)|(six)|(seven)|(eight)|(nine)|(1)|(2)|(3)|(4)|(5)|(6)|(7)|(8)|(9)")
    private val reverseRegex = Regex("(eno)|(otw)|(eerht)|(ruof)|(evif)|(xis)|(neves)|(thgie)|(enin)|(1)|(2)|(3)|(4)|(5)|(6)|(7)|(8)|(9)")

    private fun String.firstDigit(): Int = digitRegex.findAll(this).first().value.toDigit()

    // have to do the other way since there could be overlap: ex: twoone -> 21
    private fun String.lastDigit(): Int = reverseRegex.findAll(this.reversed()).first().value.toDigit()

    private fun String.toDigit(): Int {
        return when (this) {
            "1", "one", "eno" -> 1
            "2", "two", "owt" -> 2
            "3", "three", "eerht" -> 3
            "4", "four", "ruof" -> 4
            "5", "five", "evif" -> 5
            "6", "six", "xis" -> 6
            "7", "seven", "neves" -> 7
            "8", "eight", "thgie" -> 8
            "9", "nine", "enin" -> 9
            else -> throw IllegalArgumentException("Could not convert $this to a digit")
        }
    }
}
