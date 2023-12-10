package com.zachjones.adventofcode.year2022

fun main() {
    val content = FileReader2022.readFile(day = 21, example = true).split('\n')
    val monkeysPart1: Map<String, TalkingMonkey> = content.map { line ->
        val split = line.split(": ")
        val name = split[0]
        val monkey = if (split[1].toLongOrNull() != null) {
            NumberMonkey(split[1])
        } else {
            MathMonkey(split[1])
        }
        name to monkey
    }.associate { it }

    val root = monkeysPart1["root"]!!
    println("Part 1: Monkey root is speaking ${root.speak(monkeysPart1)}")

    println("\nPart 2: Monkey root is an equality operator, you are 'humn', and you need to figure out your number")
    val monkeysPart2: Map<String, TalkingMonkey> = content.map { line ->
        val split = line.split(": ")
        val name = split[0]
        val monkey = if (name == "root") {
            RootMonkey(split[1])
        } else if (name == "humn") {
            HumanMonkey()
        } else if (split[1].toLongOrNull() != null) {
            NumberMonkey(split[1])
        } else {
            MathMonkey(split[1])
        }
        name to monkey
    }.associate { it }

    solvePart2UsingEquation(monkeysPart2)
}

private fun solvePart2UsingEquation(monkeys: Map<String, TalkingMonkey>) {
    println("Solving using the equation: (bring your own algebra solver)")
    val rootMonkey: RootMonkey = monkeys.values.filterIsInstance<RootMonkey>().first()
    println("To solve for the solution, use this equation below and plug into an algebra solver:")
    println("Note: mathpapa.com will give you a slightly wrong answer on my input, I needed to take the result, 8518368129051974/2175 and use Math.round(8518368129051974.0/2175.0)")
    println(rootMonkey.getEquation(monkeys))
}

private sealed interface TalkingMonkey {
    fun speak(monkeys: Map<String, TalkingMonkey>): Long
    fun getEquation(monkeys: Map<String, TalkingMonkey>): String
}

private class HumanMonkey : TalkingMonkey {
    var number: Long = 0
    override fun speak(monkeys: Map<String, TalkingMonkey>) = number
    override fun getEquation(monkeys: Map<String, TalkingMonkey>): String = "x"
}

private class NumberMonkey(input: String) : TalkingMonkey {
    val number = input.toLong()

    override fun speak(monkeys: Map<String, TalkingMonkey>): Long {
        return number
    }

    override fun getEquation(monkeys: Map<String, TalkingMonkey>): String = "$number"
}

private class RootMonkey(input: String) : TalkingMonkey {
    val left: String
    val right: String

    init {
        val split = input.trim().split(' ')
        left = split[0]
        // operator in the input doesn't matter
        right = split[2]
    }

    override fun speak(monkeys: Map<String, TalkingMonkey>): Long {
        return if (monkeys[left]!!.speak(monkeys) == monkeys[right]!!.speak(monkeys)) {
            1
        } else {
            0
        }
    }

    override fun getEquation(monkeys: Map<String, TalkingMonkey>): String {
        val leftMonkey = monkeys[left]!!
        val rightMonkey = monkeys[right]!!
        return "(${leftMonkey.getEquation(monkeys)}=${rightMonkey.getEquation(monkeys)})"
    }
}

private class MathMonkey(input: String) : TalkingMonkey {
    val left: String
    val operator: String
    val right: String

    init {
        val split = input.trim().split(' ')
        left = split[0]
        operator = split[1]
        right = split[2]
    }

    override fun speak(monkeys: Map<String, TalkingMonkey>): Long {
        val leftSide = monkeys[left]!!.speak(monkeys)
        val rightSide = monkeys[right]!!.speak(monkeys)
        return when (operator) {
            "+" -> leftSide + rightSide
            "-" -> leftSide - rightSide
            "*" -> leftSide * rightSide
            "/" -> leftSide / rightSide
            else -> throw IllegalArgumentException("Unknown operator $operator")
        }
    }

    override fun getEquation(monkeys: Map<String, TalkingMonkey>): String {
        val leftMonkey = monkeys[left]!!
        val rightMonkey = monkeys[right]!!
        return "(${leftMonkey.getEquation(monkeys)} $operator ${rightMonkey.getEquation(monkeys)})"
    }
}
