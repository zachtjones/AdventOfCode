package com.zachjones.adventofcode

typealias Point2 = Pair<Int, Int>

fun List<Int>.product(): Int {
    var product = 1
    for (i in this) {
        product *= i
    }
    return product
}

// points here are row, column
enum class Direction2D(val delta: Point2) {
    NORTH(-1 to 0),
    EAST(0 to 1),
    SOUTH(1 to 0),
    WEST(0 to -1);

    companion object {
        fun fromCharacter(char: Char): Direction2D = when(char) {
            '^' -> NORTH
            '>' -> EAST
            '<' -> WEST
            'v', 'V' -> SOUTH
            else -> throw IllegalArgumentException("$char is not a valid Direction in 2d space")
        }
    }
}

operator fun Point2.plus(other: Point2): Point2 =
    (this.first + other.first) to (this.second + other.second)