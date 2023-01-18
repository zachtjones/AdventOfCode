package com.zachjones.adventofcode.year2022

import kotlin.math.abs

private typealias Point = Pair<Int, Int>

class Day9(isExample: Boolean): BaseChallenge2022(day = 9, isExample) {

    private val instructions = inputContent.split('\n')


    override fun solvePart1(): String {
        val snake = Snake()
        instructions.forEach { snake.executeMove(it) }

        val visited = snake.tailVisited
//        println("Part 1: snake is just head and tail")
//        println("The head ends up ${snake.head}")
//        println("The tail ends up ${snake.tail}")
//        println("The tail visited ${visited.size} locations")
        return visited.size.toString()
    }

    override fun solvePart2(): String {
        val snake2 = SnakePart2()
        instructions.forEach { snake2.executeMove(it) }
        val visited2 = snake2.tailVisited
//        println("The head ends up ${snake2.segments.first()}")
//        println("The tail ends up ${snake2.segments.last()}")
//        println("The tail visited ${visited2.size} locations")
        return visited2.size.toString()
    }
}
fun main() {
    val day = Day9(isExample = false)
    println("Part 1: ${day.solvePart1()}")
    println("Part 2: ${day.solvePart2()}")
}

private class Snake {
    // for this problem, these are x,y points
    var head: Point = 0 to 0
    var tail: Point = 0 to 0

    val tailVisited: MutableSet<Point> = hashSetOf()

    init {
        tailVisited.add(tail)
    }

    fun executeMove(rawMove: String) {
        val split = rawMove.split(' ')
        val direction = split[0]
        val count = split[1].toInt()
        repeat(count) {
            when(direction) {
                "R" -> moveRight()
                "U" -> moveUp()
                "L" -> moveLeft()
                "D" -> moveDown()
                else -> throw IllegalArgumentException("Illegal move direction: $direction")
            }
        }
    }

    private fun moveRight() {
        executeMove(head.first + 1 to head.second)
    }
    private fun moveUp() {
        executeMove(head.first to head.second - 1)
    }
    private fun moveLeft() {
        executeMove(head.first - 1 to head.second)
    }
    private fun moveDown() {
        executeMove(head.first to head.second + 1)
    }

    private fun executeMove(newHeadSpot: Point) {
        // if not touching after head moves, the tail goes where head was
        val oldHead = head
        head = newHeadSpot
        if (!isTouching()) {
            tail = oldHead
            tailVisited.add(tail)
        }
    }

    private fun isTouching(): Boolean {
        // absolute distance -- they are touching or diagonally touching
        // overlapping counts as touching
        return abs(head.first - tail.first) <= 1 && abs(head.second - tail.second) <= 1
    }

}

private const val SEGMENT_COUNT = 10

private class SnakePart2 {
    // for this problem, these are x,y points
    var segments: Array<Point> = Array(SEGMENT_COUNT) { 0 to 0 }

    val tailVisited: MutableSet<Point> = hashSetOf()

    init {
        tailVisited.add(tail())
    }

    fun executeMove(rawMove: String) {
        val split = rawMove.split(' ')
        val direction = split[0]
        val count = split[1].toInt()
        repeat(count) {
            when(direction) {
                "R" -> moveRight()
                "U" -> moveUp()
                "L" -> moveLeft()
                "D" -> moveDown()
                else -> throw IllegalArgumentException("Illegal move direction: $direction")
            }
        }
    }

    // handy helpers
    private fun head() = segments.first()
    private fun tail() = segments.last()

    private fun moveRight() {
        executeMove { point -> point.first + 1 to point.second }
    }
    private fun moveUp() {
        executeMove { point -> point.first to point.second - 1 }
    }
    private fun moveLeft() {
        executeMove { point -> point.first - 1 to point.second }
    }
    private fun moveDown() {
        executeMove { point -> point.first to point.second + 1 }
    }

    private fun executeMove(transform: (Point) -> Point) {
        // if not touching after head moves, the tail goes where head was
        // ripple throughout the snake
        val previousSpot = head()
        segments[0] = transform(previousSpot)
        for (i in 1 until segments.size) {
            if (!isTouching(i - 1, i)) {
                if (segments[i].isDiagonallyDetached(segments[i - 1])) {
                    // it needs to skip towards it's parent by moving diagonally
                    segments[i] = segments[i].moveDiagonallyToTouch(segments[i - 1])
                } else {
                    segments[i] = segments[i].moveStraightToTouch(segments[i - 1])
                }

                if (i == segments.size - 1) {
                    // the tail moved
                    tailVisited.add(tail())
                }
            }
        }

    }

    /**
     * checks to see if the two segments are touching
     * @param segment the index of the first segment
     * @param segmentTwo the index of the second segment
     */
    private fun isTouching(segment: Int, segmentTwo: Int): Boolean {
        return segments[segment].isTouching(segments[segmentTwo])
    }



}

private fun Point.isDiagonallyDetached(other: Point): Boolean {
    // assuming they are not touching
    // both coordinates are different
    return (this.first != other.first && this.second != other.second)
}

private fun Point.isTouching(other: Point): Boolean {
    return abs(this.first - other.first) <= 1
            && abs(this.second - other.second) <= 1
}

private fun Point.moveDiagonallyToTouch(parent: Point): Point {
    // check all 4 diagonal moves -- one will be touching
    val points = listOf(
        this.first + 1 to this.second + 1,
        this.first - 1 to this.second - 1,
        this.first + 1 to this.second - 1,
        this.first - 1 to this.second + 1
    )
    return points.first { parent.isTouching(it) }
}

private fun Point.moveStraightToTouch(parent: Point): Point {
    // check all 4 straight moves -- one will be touching
    val points = listOf(
        this.first + 1 to this.second,
        this.first - 1 to this.second,
        this.first to this.second - 1,
        this.first to this.second + 1
    )
    return points.first { parent.isTouching(it) }
}
