package com.zachjones.adventofcode.year2022

import com.zachjones.adventofcode.Point2
import java.util.*
import kotlin.collections.ArrayList

const val TUNNEL_WIDTH = 7

fun main() {
    val input = FileReader2022.readFile(day = 17, example = false)
    val simulation = Simulation(input)
    simulation.simulate(2022)
    val height = simulation.totalHeight()
    println("Part 1: the total height is: $height")

    println("\nPart 2: a much bigger number of rocks fall, find a pattern")
    println("Height is ${part2(input)}")
}

private fun part2(input: String): Long {


    val totalRocks: Long = 1_000_000_000_000

    // we need to find a spot where windCount
    // is a multiple of input size and we have simulated
    // a multiple of 5 rocks
    val simulation = Simulation(input)
    // enough to guarantee the modulus has cycled around
    simulation.simulate(10_000)
    var rocksSimulated = 10_000

    // because we keep simulating 5 at a time (the number of rock types)
    // we can guarantee each check for state will have the same rock type
    data class State(val numbersFromTop: List<Int>, val modulus: Int)

    val startingState = State(
        numbersFromTop = simulation.heightFromTop(),
        modulus = simulation.windCount % input.length
    )
    println(startingState)

    val rocksAtLoopStart = rocksSimulated
    val heightAtLoopStart = simulation.totalHeight()

    // loop again until I reach same mod
    println("Trying to get to $startingState")
    while(true) {
        simulation.simulate(5)
        rocksSimulated += 5

        val currentState = State(
            numbersFromTop = simulation.heightFromTop(),
            modulus = simulation.windCount % input.length
        )
        println("Current: $currentState")
        if (currentState == startingState) {
            println("Found a cycle @ ${rocksSimulated - rocksAtLoopStart} rocks later")
            break
        }

    }

    val rocksPerLoop: Long = rocksSimulated.toLong() - rocksAtLoopStart
    val totalLoops: Long = (totalRocks - rocksAtLoopStart) / rocksPerLoop
    val remainingBlocksFromClosestLoopToGoal: Long =
        (totalRocks - rocksAtLoopStart) - (totalLoops * rocksPerLoop)

    val heightGainedSinceLoop = simulation.totalHeight() - heightAtLoopStart
    repeat(remainingBlocksFromClosestLoopToGoal.toInt()) {
        simulation.simulate(1)
    }
    return simulation.totalHeight() + (heightGainedSinceLoop * (totalLoops - 1))
}


private class Simulation(
    input: String
) {

    private val movement = NextMovement(input)
    private val rocks = Rocks()

    private val stage: ArrayList<BooleanArray> = ArrayList()

    init {
        stage.add(BooleanArray(TUNNEL_WIDTH) { true })
    }

    fun simulate(numberRocks: Int) {
        repeat(numberRocks) {
            simulateRock()
        }
    }

    var windCount = 0

    private fun simulateRock() {
        val rock = rocks.nextOne()
//        println("Grabbed rock $rock")
        var rockSpots = rock.positions
            .map { it.first + 2 to it.second + 4 + totalHeight() }

        while(true) {
            // blown by the wind
            val move = movement.nextMove()
            windCount++
            if (move == Movement.LEFT) {
                if (rockSpots.all { it.canMoveLeft() && isNotOccupied(it.toTheLeft()) }) {
                    //println("Moving left")
                    rockSpots = rockSpots.map { it.toTheLeft() }
                }
            } else {
                if (rockSpots.all { it.canMoveRight() && isNotOccupied(it.toTheRight()) }) {
//                    println("Moving right")
                    rockSpots = rockSpots.map { it.toTheRight() }
                }
            }

            // move down
            if (rockSpots.all { isNotOccupied(it.downOne()) }) {
//                println("Moving down")
                rockSpots = rockSpots.map { it.downOne() }
            } else {
                // done - solidify the rock
                ensureSpace(height = rockSpots.maxOf { it.second })
                for (point in rockSpots) {
                    stage[point.second][point.first] = true
                }
                return
            }
        }
    }

    fun totalHeight(): Int {
        return stage.size - 1 // floor starts at 0, making size 1
    }

    fun heightFromTop(): List<Int> {
        return (0 until TUNNEL_WIDTH).map { x ->
            for (i in (0..totalHeight()).reversed()) {
                if (stage[i][x]) {
                    return@map totalHeight() - i
                }
            }
            return@map 0
        }
    }

    /** ensures space to place the rock in the array */
    private fun ensureSpace(height: Int) {
        while(stage.size < height + 1) {
            stage.add(BooleanArray(TUNNEL_WIDTH) { false })
        }
    }

    private fun isOccupied(point: Point2): Boolean {
        val row = stage.getOrNull(point.second) ?: return false
        return row[point.first]
    }

    private fun isNotOccupied(point: Point2) = !isOccupied(point)

    override fun toString(): String {
        val string = StringBuilder("Stage:\n|||||||\n")
        for (i in stage.indices.reversed()) {
            string.append(stage[i].joinToString(separator = "") { if (it) "#" else " " })
            string.append("\n")
        }
        return string.toString()
    }
}

class NextMovement(val input: String) {
    private val moves: LinkedList<Movement> = LinkedList(input.toCharArray()
        .map { if (it == '>') Movement.RIGHT else Movement.LEFT }
        .toList())

    fun nextMove(): Movement {
        val movement = moves.removeFirst()
        moves.addLast(movement)
        return movement
    }

    fun size() = moves.size
}
enum class Movement {
    LEFT,
    RIGHT
}

class Rocks {
    private val rocks = LinkedList<Rock>()
    init {
        rocks.add(Rock.HORIZONTAL)
        rocks.add(Rock.PLUS)
        rocks.add(Rock.BACKWARDS_L)
        rocks.add(Rock.VERTICAL)
        rocks.add(Rock.SQUARE)
    }
    fun nextOne(): Rock {
        val rock = rocks.removeFirst()
        rocks.addLast(rock)
        return rock
    }

}

private fun Point2.canMoveRight() = this.first < TUNNEL_WIDTH - 1
private fun Point2.canMoveLeft() = this.first > 0

private fun Point2.toTheRight() = this.first + 1 to this.second
private fun Point2.toTheLeft() = this.first - 1 to this.second

private fun Point2.downOne() = this.first to this.second - 1


// points are x,y
enum class Rock(vararg val positions: Point2) {
    // points are from bottom left's perspective
    HORIZONTAL(0 to 0, 1 to 0, 2 to 0, 3 to 0),
    PLUS(1 to 0, 0 to 1, 1 to 1, 2 to 1, 1 to 2),
    BACKWARDS_L(0 to 0, 1 to 0, 2 to 0, 2 to 1, 2 to 2),
    VERTICAL(0 to 0, 0 to 1, 0 to 2, 0 to 3),
    SQUARE(0 to 0, 1 to 0, 0 to 1, 1 to 1)
}
