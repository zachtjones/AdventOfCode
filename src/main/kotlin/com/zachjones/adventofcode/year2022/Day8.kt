package com.zachjones.adventofcode.year2022

import java.lang.Integer.max

private typealias Grid = List<List<Int>>

class Day8(isExample: Boolean): BaseChallenge2022(day = 8, isExample) {
    private val grid = inputContent.split('\n').map { it.toCharArray().map { it.digitToInt() } }

    override fun solvePart1(): String {
        var numberVisible = 0
        for (row in grid.indices) {
            for (column in grid[row].indices) {
                if (grid.isVisible(row, column)) {
                    numberVisible++
                }
            }
        }
        return numberVisible.toString()
    }

    override fun solvePart2(): String {
        var maxScenic = 0
        for (row in grid.indices) {
            for (column in grid[row].indices) {
                if (grid.isVisible(row, column)) {
                    maxScenic = max(maxScenic, grid.scenicScore(row, column))
                }
            }
        }
        return maxScenic.toString()
    }
}
fun main() {
    val day = Day8(isExample = false)
    println("Part 1: ${day.solvePart1()}")
    println("Part 2: ${day.solvePart2()}")
}

private fun Grid.isVisible(row: Int, column: Int): Boolean {

    if (row == 0 || column == 0) return true
    if (row == this.size || column == this[row].size) return true

    val thisValue = this[row][column]

    // go in all 4 directions
    val leftVisible = (0 until row).all { this[it][column] < thisValue }
    val rightVisible = (row + 1 until this.size).all { this[it][column] < thisValue }

    val topVisible = (0 until column).all { this[row][it] < thisValue }
    val bottomVisible = (column+1 until this[row].size).all { this[row][it] < thisValue }

    return leftVisible || rightVisible || topVisible || bottomVisible
}

private fun Grid.scenicScore(row: Int, column: Int): Int {

    val thisHeight = this[row][column]


    // go in all 4 directions
    // left
    var scoreLeft = 0
    for (r in (0 until row).reversed()) {
        scoreLeft++
        if (this[r][column] >= thisHeight) break
    }
    // right
    var scoreRight = 0
    for (r in (row+1 until this.size)) {
        scoreRight++
        if (this[r][column] >= thisHeight) break
    }

    // top
    var scoreTop = 0
    for (c in (0 until column).reversed()) {
        scoreTop++
        if (this[row][c] >= thisHeight) break
    }

    // bottom
    var scoreBottom = 0
    for (c in (column+1 until this[row].size)) {
        scoreBottom++
        if (this[row][c] == thisHeight) break
    }

    return scoreLeft * scoreRight * scoreTop * scoreBottom
}
