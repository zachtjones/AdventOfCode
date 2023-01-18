package com.zachjones.adventofcode.year2022

import java.util.LinkedList
private typealias Stack = LinkedList<Char>
private typealias Stacks = List<Stack>

class Day5(isExample: Boolean): BaseChallenge2022(day = 5, isExample) {

    private val split = inputContent.split(Regex("\n\n"))
    private val crates = split[0].removeSuffix("\n").split('\n')
    private val instructions: List<Instruction> = split[1].split('\n').map { Instruction(it) }

    private val numberOfStacks = crates
        .last() // last line
        .split(Regex("[ ]+")).last().toInt() // last number

    override fun solvePart1(): String {
        // assumptions made from this point forwards:
        // number is max 9
        require(numberOfStacks <= 9) { "This algorithm does not parse input with more than 9 stacks" }

        val stacks: Stacks = (1 .. numberOfStacks).map {
            buildStack(crates, stackNumber = it)
        }

        for (instruction in instructions) {
            instruction.execute(stacks)
        }

        return stacks.topOfStacks()
    }

    override fun solvePart2(): String {
        val stacksPart2: Stacks = (1 .. numberOfStacks).map {
            buildStack(crates, stackNumber = it)
        }

        for (instruction in instructions) {
            instruction.executePart2(stacksPart2)
        }

        return stacksPart2.topOfStacks()
    }

}
fun main() {
    val day = Day5(isExample = false)
    println("Part 1: ${day.solvePart1()}")
    println("Part 2: ${day.solvePart2()}")
}

class Instruction constructor(private val raw: String) {
    private val times: Int
    private val fromStack: Int
    private val toStack: Int

    init {
        val values = Regex("move ([0-9]+) from ([0-9]+) to ([0-9]+)").matchEntire(raw)?.groupValues
            ?: throw IllegalArgumentException("Invalid input, '$raw' does not match pattern")
        // group 0 is the entire match
        times = values[1].toInt()
        fromStack = values[2].toInt()
        toStack = values[3].toInt()
    }

    fun execute(stacks: Stacks) {
        val fromIndex = fromStack - 1
        val toIndex = toStack - 1

        repeat(times) {
            val char = stacks[fromIndex].removeLast()
            stacks[toIndex].addLast(char)
        }
    }

    fun executePart2(stacks: Stacks) {
        val fromIndex = fromStack - 1
        val toIndex = toStack - 1

        // consider the crates as a group; pop a group
        val chars = (1 ..times).map { stacks[fromIndex].removeLast() }
        stacks[toIndex].addAll(chars.reversed())
    }

    override fun toString(): String = raw
}

/**
 * Builds the stack from the input; the right end is the top of the 'stack'
 */
private fun buildStack(crates: List<String>, stackNumber: Int): Stack {
    val column: Int = crates.last().indexOf("$stackNumber")
    val items = crates.dropLast(1)

    val stack = LinkedList<Char>()
    for (row in (items.indices).reversed()) {
        val char = crates[row].getOrNull(column) // null if string is too short, there's no entry
        if (char == null || char == ' ') break
        stack.addLast(char)
    }
    return stack
}

fun Stacks.topOfStacks(): String {
    return this.mapNotNull { it.lastOrNull() }.joinToString(separator = "")
}