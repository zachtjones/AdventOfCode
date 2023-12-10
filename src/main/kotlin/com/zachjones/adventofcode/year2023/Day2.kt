package com.zachjones.adventofcode.year2023

class Day2(isExample: Boolean) : BaseChallenge2023(day = 2, isExample, separatePart2 = false) {

    override fun solvePart1(input: String): String {
        // only 12 red cubes, 13 green cubes, and 14 blue cubes
        val redMax = 12
        val greenMax = 13
        val blueMax = 14

        return input.split('\n').sumOf { game ->
            val id = game.removePrefix("Game ").split(':').first().toInt()

            // shown: ["3 blue", "4 red" "1 red", "2 green"]
            val shown = game.substringAfter(": ").split(';', ',')

            val possible = shown.all {
                val number = it.trim().substringBefore(' ').toInt()
                val max = when (it.trim().substringAfter(' ')) {
                    "red" -> redMax
                    "green" -> greenMax
                    "blue" -> blueMax
                    else -> throw RuntimeException("Unknown color: $it")
                }
                return@all number <= max
            }
            // sum of ones that are possible
            return@sumOf if (possible) id else 0
        }.toString()
    }

    override fun solvePart2(input: String): String {
        // sum of total power
        return input.split('\n').sumOf { game ->
            val id = game.removePrefix("Game ").split(':').first().toInt()

            // shown: ["3 blue", "4 red" "1 red", "2 green"]
            val shown = game.substringAfter(": ").split(';', ',')

            var redMax = 0
            var greenMax = 0
            var blueMax = 0

            shown.forEach {
                val number = it.trim().substringBefore(' ').toInt()
                when (it.trim().substringAfter(' ')) {
                    "red" -> redMax = maxOf(redMax, number)
                    "green" -> greenMax = maxOf(greenMax, number)
                    "blue" -> blueMax = maxOf(blueMax, number)
                    else -> throw RuntimeException("Unknown color: $it")
                }
            }

            // sum of ones that are possible - sum can get pretty big
            return@sumOf (redMax * greenMax * blueMax).toLong()
        }.toString()
    }
}
