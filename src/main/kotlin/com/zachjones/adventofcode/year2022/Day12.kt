package com.zachjones.adventofcode.year2022

import com.zachjones.adventofcode.Point2
import java.util.LinkedList

class Day12(isExample: Boolean): BaseChallenge2022(day = 12, isExample) {
    private val cells = inputContent.split('\n').map { it.toCharArray() }
    private val map: MutableMap<Point2, Location> = hashMapOf()
    init {
        for (x in cells.indices) {
            for (y in cells[x].indices) {
                map[x to y] = Location(x, y, cells[x][y])
            }
        }
    }

    override fun solvePart1(): String {
        val startPart1 = map.values.first { it.isStart() }
        val isEndPart1 = { it: Location -> it.isEnd() }
        val neighborsProviderPart1 = Location::potentialNeighbors
        val result = breadthFirstSearch(map, startPart1, isEndPart1, neighborsProviderPart1)
        //println("Found the goal ${result.location}")
        //println("It takes ${result.distance} steps to get there")
        return result.distance.toString()
    }

    override fun solvePart2(): String {
        val startPart2 = map.values.first { it.isEnd() }
        // reached a goal when it is at the lowest height
        val isEndPart2 = { it: Location -> it.height == 0 }
        val neighborsProviderPart2 = Location::potentialNeighborsPart2
        val resultPart2 = breadthFirstSearch(map, startPart2, isEndPart2, neighborsProviderPart2)
        //println("Found the closest starting spot ${resultPart2.location}")
        //println("It takes ${resultPart2.distance} steps to get there")
        return resultPart2.distance.toString()
    }
}
fun main() {
    val day = Day12(isExample = false)
    println("Part 1: ${day.solvePart1()}")
    println("Part 2: ${day.solvePart2()}")
}

private fun breadthFirstSearch(
    map: Map<Point2, Location>,
    start: Location,
    isGoal: (Location) -> Boolean,
    neighborsProvider: (Location, Map<Point2, Location>) -> List<Location>
): QueueEntry {

    val queue = LinkedList<QueueEntry>()
    queue.add(QueueEntry(start, distance = 0))
    val visited = HashSet<Location>()

    while(!queue.isEmpty()) {
        val (item, distance) = queue.removeFirst()
        visited.add(item)
        // found it!
        if (isGoal(item)) {
            return QueueEntry(item, distance)
        }

        val neighbors = neighborsProvider(item, map)
        queue.addAll(neighbors
            .map { QueueEntry(it, distance = distance + 1) }
            .filter { it.location !in visited }
            .onEach { visited.add(it.location) }
        )
    }
    throw IllegalArgumentException("Did not find a solution")
}

private data class QueueEntry(val location: Location, val distance: Int)

private data class Location(val x: Int, val y: Int, val character: Char) {

    val height: Int = if (isStart()) {
        0
    } else if (isEnd()) {
        'z' - 'a'
    } else {
        character - 'a'
    }
    fun isStart() = character == 'S'
    fun isEnd() = character == 'E'

    private fun surroundingPoints() = listOf(
        x to y + 1,
        x to y - 1,
        x + 1 to y,
        x - 1 to y
    )

    /** Potential neighbors in the graph search */
    fun potentialNeighbors(map: Map<Point2, Location>): List<Location> {
        return surroundingPoints()
            .mapNotNull {
                // since we use a map this will return null if the point is out of bounds
                map[it]
            }.filter {
                // up 1 max
                it.height <= this.height + 1
            }
    }

    fun potentialNeighborsPart2(map: Map<Point2, Location>): List<Location> {
        return surroundingPoints()
            .mapNotNull {
                map[it]
            }.filter {
                // down 1 max
                it.height >= this.height - 1
            }
    }
}