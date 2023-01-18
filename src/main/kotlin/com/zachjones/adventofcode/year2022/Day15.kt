package com.zachjones.adventofcode.year2022

import com.zachjones.adventofcode.Point2
import kotlin.math.abs

class Day15(isExample: Boolean): BaseChallenge2022(day = 15, isExample) {

    private val yToCheck = if (isExample) 10 else 2_000_000
    private val searchSpaceCap = if (isExample) 20 else 4_000_000
    private val sensors = parseInput(inputContent)
    private val knownBeaconSpots = sensors.map { it.nearestBeacon }.toSet()


    override fun solvePart1(): String {
        val minimumX: Int = sensors.minOf { it.point.first - it.manhattanDistance } + 2
        val maximumX: Int = sensors.maxOf { it.point.first + it.manhattanDistance } + 2
        //println("We have to check spots from $minimumX to $maximumX")

        val countSpotsTaken = (minimumX..maximumX).count {
            val point = it to yToCheck
            couldNotHaveABeacon(point, sensors, knownBeaconSpots)
        }
        return countSpotsTaken.toString()
    }

    override fun solvePart2(): String {
        val beaconSpot = findBeaconSpot(sensors, knownBeaconSpots)
        println("Beacon spot is $beaconSpot")
        return (beaconSpot.first.toLong() * 4_000_000L + beaconSpot.second.toLong()).toString()
    }


    private fun findBeaconSpot(
        sensors: List<Sensor>,
        knownBeacons: Set<Point2>
    ): Point2 {
        return findBeaconSpot(
            sensors = sensors,
            knownBeacons = knownBeacons,
            minX = 0,
            minY = 0,
            maxX = searchSpaceCap,
            maxY = searchSpaceCap
        ) ?: throw IllegalArgumentException("Could not find a suitable spot in the grid")
    }

    // recursive function
    private fun findBeaconSpot(
        sensors: List<Sensor>,
        knownBeacons: Set<Point2>,
        minX: Int,
        minY: Int,
        maxX: Int,
        maxY: Int
    ): Point2? {
        // base case: We have a line -- let's just iterate through all potentials
        if (minX == maxX || minY == maxY) {
            for (x in minX..maxX) {
                for(y in minY.. maxY) {
                    val point = x to y
                    val obstruction = obstructingSensor(point, sensors)
                    if (obstruction == null && point !in knownBeacons) {
                        return point
                    }
                }
            }
            return null
        }

        // other base case: any sensor completely covers this space
        val point1 = minX to minY
        val point2 = maxX to maxY
        if (sensors.any { it.completelyCoversBoundingBox(point1, point2) }) {
            return null
        }

        // since we have manhattan distances, we should divide into 4 squares
        // that should help the most with the completely covers removing ones
        val midX = (minX + maxX) / 2
        val midY = (minY + maxY) / 2
        /*
        * +++++++++
        * | 1 | 2 |
        * +++++++++
        * | 3 | 4 |
        * +++++++++
        * */
        val fourSpots: List<Pair<Point2, Point2>> = listOf(
            (minX to minY) to (midX to midY), // square 1
            (midX + 1 to minY) to (maxX + 1 to midY), // square 2
            (minX to midY + 1) to (midX to maxY), // square 3
            (midX + 1 to midY + 1) to (maxX to maxY) // square 4
        )
        return fourSpots.firstNotNullOfOrNull {
            val (minPoint, maxPoint) = it
            val (minXInner, minYInner) = minPoint
            val (maxXInner, maxYInner) = maxPoint
            findBeaconSpot(
                sensors = sensors,
                knownBeacons = knownBeacons,
                minX = minXInner,
                minY = minYInner,
                maxX = maxXInner,
                maxY = maxYInner
            )
        }
    }
}



fun main() {
    val day = Day15(isExample = false)
    println("Part 1: ${day.solvePart1()}")
    println("Part 2: ${day.solvePart2()}")
}


private fun couldNotHaveABeacon(
    point: Point2,
    sensors: List<Sensor>,
    knownBeacons: Set<Point2>
): Boolean {
    if (point in knownBeacons) return false
    return sensors.any { sensor -> sensor.isNewPointCloserThanBeacon(point) }
}
private fun obstructingSensor(
    point: Point2,
    sensors: List<Sensor>
): Sensor? {
    return sensors.firstOrNull() { sensor -> sensor.isNewPointCloserThanBeacon(point) }
}

private fun parseInput(input: String): List<Sensor> {
    val lines = input.split('\n')
    val sensors = lines.map {
        val searchResults = Regex("Sensor at x=(.*), y=(.*): closest beacon is at x=(.*), y=(.*)")
            .find(it)!!
            .groupValues
        val sensorX = searchResults[1].toInt()
        val sensorY = searchResults[2].toInt()
        val beaconX = searchResults[3].toInt()
        val beaconY = searchResults[4].toInt()
        Sensor(sensorX to sensorY, beaconX to beaconY)
    }
    return sensors
}

private class Sensor(
    val point: Point2,
    val nearestBeacon: Point2
) {
    val manhattanDistance = point.manhattanDistanceTo(nearestBeacon)

    fun isNewPointCloserThanBeacon(point2: Point2) =
        point.manhattanDistanceTo(point2) <= manhattanDistance

    fun completelyCoversBoundingBox(point1: Point2, point2: Point2): Boolean {
        val fourPoints = listOf(
            point1.first to point1.second,
            point1.first to point2.second,
            point2.first to point2.second,
            point2.first to point1.second
        )
        return fourPoints.all { isNewPointCloserThanBeacon(it) }
    }
}

private fun Point2.manhattanDistanceTo(point: Point2): Int {
    return abs(point.first - this.first) + abs(point.second - this.second)
}