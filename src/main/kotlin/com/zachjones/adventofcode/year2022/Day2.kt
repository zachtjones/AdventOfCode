package com.zachjones.adventofcode.year2022

class Day2(isExample: Boolean): BaseChallenge2022(day = 2, isExample) {

    private val moves = inputContent.split('\n')

    override fun solvePart1(): String {
        val totalScore = moves.map { move ->
            val them = fromMove(move.split(' ')[0])
            val us = fromCounterMove(move.split(' ')[1])
            // match score + our shape score
            return@map priority(them, us).priority() + us.priority()
        }.sum()
        return totalScore.toString()
    }

    override fun solvePart2(): String {
        // part 2 - the 2nd column is now the goal
        val totalScorePart2 = moves.map { move ->
            val them = fromMove(move.split(' ')[0])
            val goalResult: Result = toResult(move.split(' ')[1])
            return@map goalResult.priority() + yourMove(them, goalResult).priority()
        }.sum()
        return totalScorePart2.toString()
    }

}
fun main() {
    val day = Day2(isExample = false)
    println("Part 1: ${day.solvePart1()}")
    println("Part 2: ${day.solvePart2()}")
}

private enum class Shape {
    ROCK, PAPER, SCISSORS
}

private fun Shape.priority(): Int = when(this){
    Shape.ROCK -> 1
    Shape.PAPER -> 2
    Shape.SCISSORS -> 3
}

private fun fromCounterMove(move: String): Shape = when(move) {
    "X" -> Shape.ROCK
    "Y" -> Shape.PAPER
    "Z" -> Shape.SCISSORS
    else -> throw IllegalArgumentException("Invalid counter move:$move")
}

private fun fromMove(move: String): Shape = when(move) {
    "A" -> Shape.ROCK
    "B" -> Shape.PAPER
    "C" -> Shape.SCISSORS
    else -> throw IllegalArgumentException("Invalid move:$move")
}

private enum class Result {
    WIN, TIE, LOSE
}
private fun Result.priority(): Int = when(this){
    Result.WIN -> 6
    Result.TIE -> 3
    Result.LOSE -> 0
}
private fun toResult(result: String): Result = when(result) {
    "X" -> Result.LOSE
    "Y" -> Result.TIE
    "Z" -> Result.WIN
    else -> throw IllegalArgumentException("Invalid result:$result")
}


private fun priority(them: Shape, us: Shape): Result {
    return when (them) {
        Shape.ROCK -> when (us) {
            Shape.ROCK -> Result.TIE
            Shape.PAPER -> Result.WIN
            Shape.SCISSORS -> Result.LOSE
        }
        Shape.PAPER -> when (us) {
            Shape.ROCK -> Result.LOSE
            Shape.PAPER -> Result.TIE
            Shape.SCISSORS -> Result.WIN
        }
        Shape.SCISSORS -> when (us) {
            Shape.ROCK -> Result.WIN
            Shape.PAPER -> Result.LOSE
            Shape.SCISSORS -> Result.TIE
        }
    }
}

private fun yourMove(them: Shape, goal: Result): Shape {
    return when (them) {
        Shape.ROCK -> when (goal) {
            Result.WIN -> Shape.PAPER
            Result.TIE -> Shape.ROCK
            Result.LOSE -> Shape.SCISSORS
        }
        Shape.PAPER -> when (goal) {
            Result.WIN -> Shape.SCISSORS
            Result.TIE -> Shape.PAPER
            Result.LOSE -> Shape.ROCK
        }
        Shape.SCISSORS -> when (goal) {
            Result.WIN -> Shape.ROCK
            Result.TIE -> Shape.SCISSORS
            Result.LOSE -> Shape.PAPER
        }
    }
}