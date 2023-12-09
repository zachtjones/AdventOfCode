package com.zachjones.adventofcode.year2022

import com.zachjones.adventofcode.Direction2D
import com.zachjones.adventofcode.Point2
import com.zachjones.adventofcode.plus

fun main() {
    val input = FileReader.readFile(day = 24, example = true)
    val blizzards = Blizzards(input)
    blizzards.solve()
}

// points are all row, column
// 0, 0 is top left (which is a wall)
private class Blizzards(input: String) {
    val startingBlizzards: List<Blizzard>
    val walls: List<Point2>
    val start: Point2
    val goal: Point2
    val wallMinRow = 0
    val wallMinColumn = 0
    val wallMaxRow: Int
    val wallMaxColumn: Int

    init {
        val lines = input.split('\n')
        start = 0 to lines.first().indexOf('.')
        goal = lines.count() - 1 to lines.last().indexOf('.')

        val wallsSetup = ArrayList<Point2>()
        val blizzardSetup = ArrayList<Blizzard>()

        lines.forEachIndexed { row, line ->
            line.forEachIndexed { column, character ->
                when (character) {
                    '#' -> wallsSetup.add(row to column)
                    '.' -> {} // empty spot
                    else -> blizzardSetup.add(
                        Blizzard(
                            point = row to column,
                            direction = Direction2D.fromCharacter(character),
                        ),
                    )
                }
            }
        }
        walls = wallsSetup
        startingBlizzards = blizzardSetup
        wallMaxRow = lines.size - 1
        wallMaxColumn = lines.first().length - 1
    }

    fun simulateBlizzardsMoving(current: List<Blizzard>): List<Blizzard> {
        return current.map {
            var newPoint = it.direction.delta + it.point
            // adjust for wrap around
            if (newPoint.first == wallMinRow) {
                newPoint = wallMaxRow - 1 to newPoint.second
            } else if (newPoint.first == wallMaxRow) {
                newPoint = wallMinRow + 1 to newPoint.second
            } else if (newPoint.second == wallMinColumn) {
                newPoint = newPoint.first to wallMaxColumn - 1
            } else if (newPoint.second == wallMaxColumn) {
                newPoint = newPoint.first to wallMinColumn + 1
            }
            Blizzard(point = newPoint, direction = it.direction)
        }
    }

    fun solve() {
        println(start)
        println(goal)
        println(startingBlizzards)
        var step = startingBlizzards
        // the step seems to be right so that's not too bad
        for (i in 0..4) {
            step = simulateBlizzardsMoving(step)
            println(step)
        }

        // this is going to be using A* algorithm, https://en.wikipedia.org/wiki/A*_search_algorithm

        // returns the number of minutes needed to reach the goal

        /* function reconstruct_path(cameFrom, current)
    total_path := {current}
    while current in cameFrom.Keys:
        current := cameFrom[current]
        total_path.prepend(current)
    return total_path

// A* finds a path from start to goal.
// h is the heuristic function. h(n) estimates the cost to reach goal from node n.
function A_Star(start, goal, h)
    // The set of discovered nodes that may need to be (re-)expanded.
    // Initially, only the start node is known.
    // This is usually implemented as a min-heap or priority queue rather than a hash-set.
    openSet := {start}

    // For node n, cameFrom[n] is the node immediately preceding it on the cheapest path from start
    // to n currently known.
    cameFrom := an empty map

    // For node n, gScore[n] is the cost of the cheapest path from start to n currently known.
    gScore := map with default value of Infinity
    gScore[start] := 0

    // For node n, fScore[n] := gScore[n] + h(n). fScore[n] represents our current best guess as to
    // how cheap a path could be from start to finish if it goes through n.
    fScore := map with default value of Infinity
    fScore[start] := h(start)

    while openSet is not empty
        // This operation can occur in O(Log(N)) time if openSet is a min-heap or a priority queue
        current := the node in openSet having the lowest fScore[] value
        if current = goal
            return reconstruct_path(cameFrom, current)

        openSet.Remove(current)
        for each neighbor of current
            // d(current,neighbor) is the weight of the edge from current to neighbor
            // tentative_gScore is the distance from start to the neighbor through current
            tentative_gScore := gScore[current] + d(current, neighbor)
            if tentative_gScore < gScore[neighbor]
                // This path to neighbor is better than any previous one. Record it!
                cameFrom[neighbor] := current
                gScore[neighbor] := tentative_gScore
                fScore[neighbor] := tentative_gScore + h(neighbor)
                if neighbor not in openSet
                    openSet.add(neighbor)

    // Open set is empty but goal was never reached
    return failure*/
    }
}

private data class Blizzard(val point: Point2, val direction: Direction2D)
