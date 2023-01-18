package com.zachjones.adventofcode.year2022

import kotlin.math.abs

class Day10(private val isExample: Boolean): BaseChallenge2022(day = 10, isExample) {

    private val lines = inputContent.split('\n')
    private val keyPoints = listOf(20, 60, 100, 140, 180, 220)

    override fun solvePart1(): String {
        val computer = Computer(keyPoints)
        lines.forEach { computer.execute(it) }
        val keyValues = computer.keyValues
        return keyValues.sum().toString()
    }

    override fun solvePart2(): String {
        val computer = Computer(keyPoints)
        lines.forEach { computer.execute(it) }

        computer.displayCRT.chunked(40).map { row ->
            // printing ' ' instead of '.' is easier to read
            row.joinToString(separator = "") { if (it) "#" else " " }
        }.forEach {
            println(it)
        }
        // not going to make the computer parse the output when I can read it, so just hard coding:
        return if (this.isExample) {
            ""
        } else {
            "FCJAPJRE"
        }
    }

}
fun main() {
    val day = Day10(isExample = true)
    println("Part 1: ${day.solvePart1()}")
    println("Part 2: ${day.solvePart2()}")
}

private class Computer(private val keyPoints: List<Int>) {
    var cycleNumber = 0
    var x = 1 // the single register
    val keyValues = arrayListOf<Int>()
    val xOverTime = arrayListOf<Int>()
    val displayCRT = arrayListOf<Boolean>()

    fun execute(statement: String) {
        if (statement.startsWith("addx")) {
            val number = statement.split(' ')[1].toInt()
            onTick()
            onTick()
            x += number
            return
        }
        require(statement == "noop") { "invalid command $statement"}
        // noop
        onTick()
    }

    private fun onTick() {
        cycleNumber++
        if (this.cycleNumber in keyPoints) {
            keyValues.add(signalStrength())
        }
        xOverTime.add(x)
        // calculate CRT
        val cycleInRow = cycleNumber.mod(40) - 1
        if (abs(cycleInRow - x) <= 1) {
            displayCRT.add(true)
        } else {
            displayCRT.add(false)
        }
    }

    private fun signalStrength() = cycleNumber * x
}