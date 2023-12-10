package com.zachjones.adventofcode.year2022

import com.zachjones.adventofcode.Point2

fun main() {
    val isExample = true

    val input = FileReader2022.readFile(day = 22, example = isExample)
    val board = BoardPart1(input)
    println("Part 1: The score is ${board.simulate()}")

    // note: there's a face that is off for this one so it is not currently working
    val board2 = BoardPart2(input, isExample)
    // answer for mine is 95291 for the real one, 5031 for example
    // TODO - make this one work for the example as well
    println("Part 2: The score is ${board2.simulate()}")
}

private abstract class BaseBoard(input: String) {
    // points here are row, column
    val places = HashMap<Point2, Spot>()
    val instructions: List<String>

    var currentSpot: Point2
    var facing = Facing.RIGHT

    init {
        val lines = input.split('\n').dropLast(2)
        lines.mapIndexed { r, line ->
            val row = r + 1
            line.mapIndexed { c, char ->
                val column = c + 1
                if (char == '.') {
                    places[row to column] = Spot.OPEN
                }
                if (char == '#') {
                    places[row to column] = Spot.WALL
                }
            }
        }
        val startingRow = 1
        val startingColumn = lines.first().indexOfFirst { it == '.' }
        currentSpot = startingRow to startingColumn

        instructions = input.split('\n').last()
            .replace("R", " R ") // add spaces to make it easier to split
            .replace("L", " L ")
            .split(' ')
    }

    fun simulate(): Int {
        for (move in instructions) {
            simulateMove(move)
        }
        val (row, column) = this.currentSpot
        println("$row $column ${facing.points}")
        return 1000 * row + 4 * column + facing.points
    }

    private fun simulateMove(move: String) {
        when (move) {
            "R" -> turnRight()
            "L" -> turnLeft()
            else -> {
                val steps = move.toInt()
                repeat(steps) {
                    stepForward()
                }
            }
        }
    }

    private fun turnRight() {
        facing = when (facing) {
            Facing.RIGHT -> Facing.DOWN
            Facing.DOWN -> Facing.LEFT
            Facing.LEFT -> Facing.UP
            Facing.UP -> Facing.RIGHT
        }
    }
    private fun turnLeft() {
        facing = when (facing) {
            Facing.RIGHT -> Facing.UP
            Facing.DOWN -> Facing.RIGHT
            Facing.LEFT -> Facing.DOWN
            Facing.UP -> Facing.LEFT
        }
    }

    private fun stepForward() {
        val targetPoint = when (facing) {
            Facing.RIGHT -> currentSpot.first to currentSpot.second + 1
            Facing.DOWN -> currentSpot.first + 1 to currentSpot.second
            Facing.LEFT -> currentSpot.first to currentSpot.second - 1
            Facing.UP -> currentSpot.first - 1 to currentSpot.second
        }
        // simple case - spot exists and is available
        if (places.containsKey(targetPoint) && places.get(targetPoint)!! == Spot.OPEN) {
            currentSpot = targetPoint
            return
        }
        // simple case 2 -- spot exists but is wall, you just stay put
        if (places.containsKey(targetPoint)) {
            return
        }
        // have to calculate the wrap around spot
        val (newSpot, newFacing) = wrap()
        currentSpot = newSpot
        facing = newFacing
    }

    abstract fun wrap(): Pair<Point2, Facing>
}

private class BoardPart1(input: String) : BaseBoard(input) {

    override fun wrap(): Pair<Point2, Facing> {
        val newPoint = when (facing) {
            Facing.RIGHT -> {
                val row = currentSpot.first
                // find the least column that is empty
                val column = places.keys.filter { it.first == row }.minOf { it.second }
                if (places[row to column] == Spot.WALL) {
                    currentSpot
                } else {
                    (row to column)
                }
            }
            Facing.DOWN -> {
                val column = currentSpot.second
                // find the least row that is empty
                val row = places.keys.filter { it.second == column }.minOf { it.first }
                if (places[row to column] == Spot.WALL) {
                    currentSpot
                } else {
                    row to column
                }
            }
            Facing.LEFT -> {
                val row = currentSpot.first
                // find the biggest column that is on the board
                val column = places.keys.filter { it.first == row }.maxOf { it.second }
                if (places[row to column] == Spot.WALL) {
                    currentSpot
                } else {
                    row to column
                }
            }
            Facing.UP -> {
                val column = currentSpot.second
                // find the biggest row that is on the board
                val row = places.keys.filter { it.second == column }.maxOf { it.first }
                if (places[row to column] == Spot.WALL) {
                    currentSpot
                } else {
                    row to column
                }
            }
        }
        return newPoint to facing
    }
}

private class BoardPart2(input: String, val isExample: Boolean) : BaseBoard(input) {

    private val size = if (isExample) 4 else 50

    // note: this does not support the example size very well
    override fun wrap(): Pair<Point2, Facing> {
        val (row, column) = currentSpot
        val newRow: Int
        val newColumn: Int
        val newFacing: Facing

        when (facing) {
            Facing.RIGHT -> {
                if (row <= size) {
                    newFacing = Facing.LEFT
                    newRow = (3 * size) + 1 - row
                    newColumn = (2 * size)
                } else if (row <= (2 * size)) {
                    newFacing = Facing.UP
                    newRow = size
                    newColumn = size + row
                } else if (row <= (3 * size)) {
                    newFacing = Facing.LEFT
                    newRow = 3 * size + 1 - row
                    newColumn = (3 * size)
                } else {
                    newFacing = Facing.UP
                    newRow = (3 * size)
                    newColumn = row - (2 * size)
                }
            }
            Facing.DOWN -> {
                if (column <= size) {
                    newFacing = Facing.DOWN
                    newRow = 1
                    newColumn = column + (2 * size)
                } else if (column <= (2 * size)) {
                    newFacing = Facing.LEFT
                    newRow = column + (2 * size)
                    newColumn = size
                } else {
                    newFacing = Facing.LEFT
                    newRow = column - size
                    newColumn = (2 * size)
                }
            }
            Facing.LEFT -> {
                if (row <= size) {
                    newFacing = Facing.RIGHT
                    newRow = 3 * size + 1 - row
                    newColumn = 1
                } else if (row <= (2 * size)) {
                    newFacing = Facing.DOWN
                    newRow = 2 * size + 1
                    newColumn = row - size
                } else if (row <= (3 * size)) {
                    newFacing = Facing.RIGHT
                    newRow = 3 * size + 1 - row
                    newColumn = size + 1
                } else {
                    newFacing = Facing.DOWN
                    newRow = 1
                    newColumn = row - (2 * size)
                }
            }
            Facing.UP -> {
                if (column <= size) {
                    newFacing = Facing.RIGHT
                    newRow = column + size
                    newColumn = size + 1
                } else if (column <= 2 * size) {
                    newFacing = Facing.RIGHT
                    newRow = column + (2 * size)
                    newColumn = 1
                } else {
                    newFacing = Facing.UP
                    newRow = 4 * size
                    newColumn = column - (2 * size)
                }
            }
        }

        if (places[newRow to newColumn] == Spot.OPEN) {
            return (newRow to newColumn) to newFacing
        }
        return currentSpot to facing // did not move
    }
}

private enum class Spot {
    OPEN, WALL
}
private enum class Facing(val points: Int) {
    RIGHT(0),
    DOWN(1),
    LEFT(2),
    UP(3),
}
