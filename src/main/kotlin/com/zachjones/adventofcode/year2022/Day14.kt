package com.zachjones.adventofcode.year2022

import com.zachjones.adventofcode.Point2
import kotlin.math.max

private const val SAND_LOCATION_X = 500
private const val SAND_LOCATION_Y = 0

class Day14(isExample: Boolean): BaseChallenge2022(day = 14, isExample) {
    override fun solvePart1(): String {
        val (rockLocations, maximumYCoordinate) = parseInput(inputContent.split('\n'))

        // sand will flow into the abyss after one falls below max y
        val sand = simulateSandFalling(rockLocations, maximumYCoordinate)
        return sand.size.toString()
    }

    override fun solvePart2(): String {
        val (rockLocations, maximumYCoordinate) = parseInput(inputContent.split('\n'))
        val rockLocations2 = HashSet(rockLocations)
        // going 1 extra to the left and right to be safe
        (SAND_LOCATION_X - (maximumYCoordinate + 3) .. SAND_LOCATION_X + (maximumYCoordinate + 3)).forEach { x ->
            rockLocations2.add(x to (maximumYCoordinate + 2))
        }
        //println("The maximum y observed is $maximumYCoordinate, bring the floor to ${maximumYCoordinate+2}")
        //println("This will take a few seconds...")
        val sand2 = simulateSandFalling(rockLocations2, maximumYCoordinate + 2)
        //println("There will be ${sand2.size} grains of sand when it stops")
        return sand2.size.toString()
    }
}
fun main() {
    val day = Day14(isExample = false)
    println("Part 1: ${day.solvePart1()}")
    println("Part 2: ${day.solvePart2()}")
}

private fun simulateSandFalling(rockLocations: Set<Point2>, maximumYCoordinate: Int): Set<Point2> {
    val sandAtRest = hashSetOf<Point2>()
    // although this is not in the instructions, it should also stop when the sand can't
    // fall anymore since it filled up with sand
    while(sandAtRest.doesNotContain(SAND_LOCATION_X to SAND_LOCATION_Y)) {
        var fallingSand = Point2(SAND_LOCATION_X, SAND_LOCATION_Y)
        val taken = rockLocations + sandAtRest
        while(fallingSand.canFall(taken)) {
            fallingSand = fallingSand.fallLocation(taken)!!
            if (fallingSand.second >= maximumYCoordinate) {
                // done
                return sandAtRest
            }
        }
        sandAtRest.add(fallingSand)
    }
    return sandAtRest
}

// there's definitely a lot of duplicate execution but I wanted cleaner code up above;
// this can definitely be improved with more time
// the whole program also runs still pretty fast (<5 seconds) so it's ok
fun Point2.canFall(blockers: Set<Point2>): Boolean = this.fallLocation(blockers) != null

fun Point2.fallLocation(blockers: Set<Point2>): Point2? {
    if (blockers.doesNotContain(this.downOne())) {
        return this.downOne()
    }
    if (blockers.doesNotContain(this.downAndLeft())) {
        return this.downAndLeft()
    }
    if (blockers.doesNotContain(this.downAndRight())) {
        return this.downAndRight()
    }
    // this is blocked
    return null
}

private fun Point2.downOne(): Point2 = Point2(this.first, this.second + 1)
private fun Point2.downAndLeft(): Point2 = Point2(this.first - 1, this.second + 1)
private fun Point2.downAndRight(): Point2 = Point2(this.first + 1, this.second + 1)

fun <T> Set<T>.doesNotContain(entry: T) = !this.contains(entry)

// returns the set of points drawn out by the lines in the input
private fun parseInput(lineGroups: List<String>): Pair<Set<Point2>, Int> {
    val rockLocations = HashSet<Point2>()
    var maximumYCoordinate = 0

    for (line in lineGroups) {
        val coordinates = line.split(Regex(" -> "))
        for (pairs in coordinates.windowed(2)) {
            val point1 = pairs[0].toPoint()
            val point2 = pairs[1].toPoint()
            rockLocations.addAll(point1 through point2)

            // keep track of the maximum y coordinate as we go
            maximumYCoordinate = max(maximumYCoordinate, point1.second)
            maximumYCoordinate = max(maximumYCoordinate, point2.second)
        }
    }
    return rockLocations to maximumYCoordinate
}

private fun String.toPoint(): Point2 {
    val split = this.split(',')
    return Point2(split[0].toInt(), split[1].toInt())
}

// all points from one to the other -- assuming they are either horizontal or vertical
private infix fun Point2.through(point2: Point2): List<Point2> {
    // assumption either x or y is the same between both
    // range operator only returns items in ascending order, so can add both directions
    //  to ensure that we always have elements
    if (this.first == point2.first) {
        return ((this.second..point2.second) + (point2.second .. this.second)).map { Point2(this.first, it) }
    }
    require(this.second == point2.second) { "Adjacent points should be horizontal or vertical"}
    return ((this.first .. point2.first) + (point2.first .. this.first)).map { Point2(it, this.second) }
}
