package com.zachjones.adventofcode.year2022

import com.zachjones.adventofcode.Point2
import com.zachjones.adventofcode.plus

fun main() {
    val input = FileReader2022.readFile(day = 23, example = false).split('\n')

    val elves = hashSetOf<Point2>()
    input.forEachIndexed { rowIndex, rowInput ->
        rowInput.forEachIndexed { columnIndex, character ->
            if (character == '#') {
                elves.add(rowIndex to columnIndex)
            }
        }
    }

    val (rectangleSize1) = simulatePart(elves, maxRounds = 10)
    println("Result for part 1: $rectangleSize1")

    val (_, turnsPart2) = simulatePart(elves, maxRounds = Integer.MAX_VALUE)
    println("Result for part 2: $turnsPart2")
}

private fun simulatePart(elves: Set<Point2>, maxRounds: Int): Pair<Int, Int> {
    val workingElves = HashSet(elves)
    val directions = DirectionsToCheck()

    var turnCounter = 0
    for (i in 1..maxRounds) {
        turnCounter++
        val directionsToCheck = directions.getDirectionsForTurn()
        val currentAndNewSpots: Map<Point2, Point2> = workingElves.associateWith { it.proposedNewSpot(workingElves, directionsToCheck) }

        var moveCounter = 0
        // resolve new spots; check for collisions
        for (entry in currentAndNewSpots) {
            val current = entry.key
            val newSpot = entry.value
            if (currentAndNewSpots.values.count { it == newSpot } == 1 && current != newSpot) {
                // can move
                workingElves.remove(current)
                workingElves.add(newSpot)
                moveCounter++
            }
        }

        // println("$moveCounter elves moved during turn $turnCounter")
        if (moveCounter == 0) {
            break
        }
    }

    // grab the 4 key values
    val minRow = workingElves.minOf { it.first }
    val maxRow = workingElves.maxOf { it.first }
    val minColumn = workingElves.minOf { it.second }
    val maxColumn = workingElves.maxOf { it.second }
    return (maxRow - minRow + 1) * (maxColumn - minColumn + 1) - elves.size to turnCounter
}

private fun Point2.proposedNewSpot(elves: Set<Point2>, directionsToCheck: List<List<Direction>>): Point2 {
    val allSpotsAreOpen = Direction.values().all {
        val newSpot = this + it.delta
        newSpot !in elves
    }
    // does not move
    if (allSpotsAreOpen) return this

    // else check the 4 pairs
    directionsToCheck.forEach { group ->
        if (group.all {
                val newSpot = this + it.delta
                newSpot !in elves
            }
        ) {
            val stepDirection = group.first()
            return this + stepDirection.delta
        }
    }
    // no match; stay put
    return this
}

private class DirectionsToCheck {
    private val directions = mutableListOf(
        listOf(Direction.NORTH, Direction.NORTH_EAST, Direction.NORTH_WEST),
        listOf(Direction.SOUTH, Direction.SOUTH_EAST, Direction.SOUTH_WEST),
        listOf(Direction.WEST, Direction.NORTH_WEST, Direction.SOUTH_WEST),
        listOf(Direction.EAST, Direction.NORTH_EAST, Direction.SOUTH_EAST),
    )

    fun getDirectionsForTurn(): List<List<Direction>> {
        // return copy so we can't mess it up
        val currentValue = ArrayList(directions)

        // cycle through
        val first = directions.removeFirst()
        directions.add(first)

        return currentValue
    }
}

// points here are row, column
enum class Direction(val delta: Point2) {
    NORTH(-1 to 0),
    NORTH_EAST(-1 to 1),
    EAST(0 to 1),
    SOUTH_EAST(1 to 1),
    SOUTH(1 to 0),
    SOUTH_WEST(1 to -1),
    WEST(0 to -1),
    NORTH_WEST(-1 to -1),
}
