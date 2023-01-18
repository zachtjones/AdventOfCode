package com.zachjones.adventofcode.year2022

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import java.lang.Integer.min

class TheyAreInOrderException: Exception()
class TheyAreOutOfOrderException: Exception()


class Day13(isExample: Boolean): BaseChallenge2022(day = 13, isExample) {


    override fun solvePart1(): String {
        val pairList: List<List<String>> = inputContent
            .split(Regex("\n\n"))
            .map { group -> group.split('\n') }

        val pairsInOrderIndexes = pairList.mapIndexedNotNull { index, it ->
            if (isInOrder(it[0], it[1])) {
                index + 1 // indexing in the puzzle starts at 1
            } else null
        }
        return pairsInOrderIndexes.sum().toString()
    }

    override fun solvePart2(): String {
        val firstSeparator = "[[2]]"
        val secondSeparator = "[[6]]"
        val itemsSorted = inputContent.split('\n')
            .filter { it != "" } // filter empties
            .toMutableList()
            .also { it.add(firstSeparator); it.add(secondSeparator) }
            .sortedWith(comparator)
        val firstSeparatorSpot = itemsSorted.indexOf(firstSeparator) + 1
        val secondSeparatorSpot = itemsSorted.indexOf(secondSeparator) + 1
        println("Separators are located at $firstSeparatorSpot and $secondSeparatorSpot")
        return (firstSeparatorSpot * secondSeparatorSpot).toString()
    }
}
fun main() {
    val day = Day13(isExample = false)
    println("Part 1: ${day.solvePart1()}")
    println("Part 2: ${day.solvePart2()}")
}

val comparator: Comparator<String> = Comparator { left, right ->
    if (isInOrder(left, right)) {
        -1
    } else {
        1
    }
    // we don't ever have any equal elements in the list
}

fun isInOrder(first: String, second: String): Boolean {
    val firstParsed = parseInput(first)
    val secondParsed = parseInput(second)

    return try {
        checkOrder(firstParsed, secondParsed)
        // processed the whole thing; in this case they are the same which is not in order
        throw IllegalArgumentException("Invalid input - 2 items are the same")
    } catch (ex: TheyAreInOrderException) {
        true
    } catch (ex: TheyAreOutOfOrderException) {
        false
    }
}
fun checkOrder(left: JsonArray, right: JsonArray) {
    for (i in 0 until min(left.size(), right.size())) {
        val leftItem = left.get(i)
        val rightItem = right.get(i)
        if (leftItem.isJsonPrimitive && rightItem.isJsonPrimitive) {
            checkOrder(leftItem.asInt, rightItem.asInt)
        } else if (leftItem.isJsonArray && rightItem.isJsonArray) {
            checkOrder(leftItem.asJsonArray, rightItem.asJsonArray)
        } else if (leftItem.isJsonPrimitive && rightItem.isJsonArray) {
            val leftAsArray = JsonArray().also { it.add(leftItem.asInt) }
            checkOrder(leftAsArray, rightItem.asJsonArray)
        } else if (leftItem.isJsonArray && rightItem.isJsonPrimitive) {
            val rightAsArray = JsonArray().also { it.add(rightItem.asInt) }
            checkOrder(leftItem.asJsonArray, rightAsArray)
        } else {
            throw IllegalArgumentException("neither arrays nor primitives detected")
        }
    }
    if (left.size() < right.size()) {
        // second ran out
        throw TheyAreInOrderException()
    }
    if (left.size() > right.size()) {
        throw TheyAreOutOfOrderException()
    }

    // same - continue
}
fun checkOrder(left: Int, right: Int) {
    if (left < right) throw TheyAreInOrderException()
    if (left > right) throw TheyAreOutOfOrderException()
    // continue processing
}

private fun parseInput(input: String): JsonArray = JsonParser().parse(input).asJsonArray
