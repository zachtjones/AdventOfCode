package com.zachjones.adventofcode.year2022

import java.math.BigInteger
import java.util.*

class Day11(isExample: Boolean): BaseChallenge2022(day = 11, isExample) {
    private val eachMonkeyInput = inputContent.split(Regex("\n\n"))
    override fun solvePart1(): String {
        return runSimulation(parseInput(eachMonkeyInput), numberRounds = 20, divide = true).toString()
    }

    override fun solvePart2(): String {
        return runSimulation(parseInput(eachMonkeyInput), numberRounds = 10_000, divide = false).toString()
    }
}

fun main() {
    val day = Day11(isExample = false)
    println("Part 1: ${day.solvePart1()}")
    println("Part 2: ${day.solvePart2()}")
}

private fun parseInput(eachMonkeyInput: List<String>): SortedMap<Int, Monkey> {
    return eachMonkeyInput
        .map { Monkey(it) }
        .associateBy { it.number }
        .toSortedMap() // so we iterate starting with Monkey 0
}

private fun runSimulation(monkeys: SortedMap<Int, Monkey>, numberRounds: Int, divide: Boolean): BigInteger {
    val three = BigInteger.valueOf(3)

    val allAreDivisible = monkeys.values
        .map { it.divisibleBy }
        .reduceRight { acc, i -> acc * i }
        .toBigInteger()

    repeat(numberRounds) {
        for (monkey in monkeys.values) {
            for (item in monkey.items) {
                // inspect & relax
                val newWorry = monkey.operation(item)
                    .let { if(divide) it / three else it }
                    .let { it % allAreDivisible }
                // then test and throw
                val test = newWorry.mod(monkey.divisibleBy.toBigInteger()) == BigInteger.ZERO
                if (test) {
                    monkeys[monkey.trueThrowTo]!!.items.add(newWorry)
                } else {
                    monkeys[monkey.falseThrowTo]!!.items.add(newWorry)
                }
            }
            monkey.inspectItemCount += monkey.items.size
            monkey.items.clear() // moved all items out
        }
    }
    return getResults(monkeys)
}

private fun getResults(monkeys: SortedMap<Int, Monkey>): BigInteger {
    val mostActive = monkeys.values.sortedBy { m -> m.inspectItemCount }.takeLast(2)
    print("Two most active are ${mostActive.map { m -> m.number }} with ")
    val inspectedCount = mostActive.map { m -> m.inspectItemCount }
    println("$inspectedCount items inspected")
    return inspectedCount[0].toBigInteger() * inspectedCount[1].toBigInteger()
}

private class Monkey(input: String) {
    val number: Int
    val items = arrayListOf<BigInteger>()
    val operation: (BigInteger) -> BigInteger
    val divisibleBy: Int
    val trueThrowTo: Int
    val falseThrowTo: Int
    var inspectItemCount: Int = 0
    init {
        number = Regex("Monkey (.*):\n")
            .find(input)!!
            .groupValues[1]
            .toInt()
        items.addAll(
            Regex("Starting items: (.*)\n")
                .find(input)!!
                .groupValues[1]
                .split(", ")
                .map { it.toBigInteger() }
        )
        val operatorGroups = Regex("Operation: new = old (.) (.*)\n")
            .find(input)!!
            .groupValues
        operation = if (operatorGroups[1] == "+") {
            if (operatorGroups[2] == "old") {
                { x: BigInteger -> x + x }
            } else {
                { x: BigInteger -> x + operatorGroups[2].toBigInteger() }
            }
        } else {
            // the * operator
            if (operatorGroups[2] == "old") {
                { x: BigInteger -> x * x }
            } else {
                { x: BigInteger -> x * operatorGroups[2].toBigInteger() }
            }
        }
        divisibleBy = Regex("Test: divisible by (.*)\n")
            .find(input)!!
            .groupValues[1]
            .toInt()
        trueThrowTo = Regex("If true: throw to monkey (.*)\n")
            .find(input)!!
            .groupValues[1]
            .toInt()
        falseThrowTo = Regex("If false: throw to monkey (.*)")
            .find(input)!!
            .groupValues[1]
            .toInt()
    }

    override fun toString(): String {
        return "Monkey: $number, items = $items"
    }

}