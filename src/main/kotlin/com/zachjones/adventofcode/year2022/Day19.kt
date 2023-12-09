package com.zachjones.adventofcode.year2022

import com.zachjones.adventofcode.product
import java.util.*
import kotlin.collections.HashSet

fun main() {
    val lines = FileReader.readFile(day = 19, example = false).split('\n')

    val blueprints = lines.map { Blueprint(it) }

    println("Part 1: Total score using sum(geode count * id number)")
    val minutesPart1 = 24
    val totalScore = blueprints.sumOf {
        // observation: each blueprint is completely separate
        // so we can compute it line by line
        print("Solving ${it.idNumber}, ... ")
        val minedGeodes = it.maximumGeodes(minutesPart1)
        println("blueprint ${it.idNumber} mined $minedGeodes")
        return@sumOf it.idNumber * minedGeodes
    }
    println("\nPart 1: Total score: $totalScore")

    println("\n\nPart 2: product of geode count of the first 3 blueprints in the list")
    val blueprintsPart2 = blueprints.take(3)
    val minutesPart2 = 32
    val scores = blueprintsPart2.map {
        print("Solving ${it.idNumber}, ... ")
        val minedGeodes = it.maximumGeodes(minutesPart2)
        println("blueprint ${it.idNumber} mined $minedGeodes")
        return@map minedGeodes
    }
    println("Scores part 2: $scores, result=${scores.product()}")
}

class Blueprint(input: String) {

    val idNumber: Int
    val oreRobotOreCost: Int
    val clayRobotOreCost: Int
    val obsidianRobotOreCost: Int
    val obsidianRobotClayCost: Int
    val geodeRobotOreCost: Int
    val geodeRobotObsidianCost: Int

    val maxOreCost: Int

    init {
        val groups = Regex(
            "Blueprint (.+): " +
                "Each ore robot costs (.+) ore. " +
                "Each clay robot costs (.+) ore. " +
                "Each obsidian robot costs (.+) ore and (.+) clay. " +
                "Each geode robot costs (.+) ore and (.+) obsidian.",
        )
            .matchEntire(input)!!
            .groupValues
        idNumber = groups[1].toInt()
        oreRobotOreCost = groups[2].toInt()
        clayRobotOreCost = groups[3].toInt()

        obsidianRobotOreCost = groups[4].toInt()
        obsidianRobotClayCost = groups[5].toInt()
        geodeRobotOreCost = groups[6].toInt()
        geodeRobotObsidianCost = groups[7].toInt()

        maxOreCost = maxOf(oreRobotOreCost, clayRobotOreCost, obsidianRobotOreCost, geodeRobotOreCost)
    }

    fun maximumGeodes(availableMinutes: Int): Int {
        return MiningSimulation(this, availableMinutes).simulate()
    }
}

private class MiningSimulation(val blueprint: Blueprint, val availableMinutes: Int) {

    /**
     * runs the simulation; returning the number of geodes opened
     */
    fun simulate(): Int {
        val startingState = MiningSimulationState(
            oreRobots = 1,
            clayRobots = 0,
            obsidianRobots = 0,
            geodeRobots = 0,
            oreCount = 0,
            clayCount = 0,
            obsidianCount = 0,
            geodeCount = 0,
            currentMinute = 1,
        )
        // graph search starting with no resources and one robot
        // use BFS
        val queue = LinkedList<MiningSimulationState>()
        val added = HashSet<MiningSimulationState>()
        val visited = HashSet<MiningSimulationState>()
        var bestSoFarGeodeCount = 0
        var bestSoFarState = startingState
        queue.add(startingState)

        while (queue.isNotEmpty()) {
            val current = queue.remove()

            // since my queue is ordered by time (due to BFS), I can discard any states where
            // I have less than the best so far number of geode mining robots
            // note: not completely sure why I have to do to this, but it works
            if (current.geodeCount < bestSoFarState.geodeCount / 2) {
                continue
            }

            visited.add(current)
            // geode robots get a final crack at some geodes right before time is up
            val currentGeodeCount = current.geodeCount + current.geodeRobots
            if (currentGeodeCount >= bestSoFarGeodeCount) {
                bestSoFarGeodeCount = currentGeodeCount
                bestSoFarState = current
            }
            // we want to keep iterating, but don't add any more children if we reach the end
            if (current.currentMinute != availableMinutes) {
                val neighbors = current.neighbors(blueprint)
                // smarter adding; so we don't have duplicates in the queue
                for (neighbor in neighbors) {
                    if (neighbor !in added) {
                        queue.add(neighbor)
                        added.add(neighbor)
                    }
                }
            }
        }

        println("Solution for ${blueprint.idNumber} is $bestSoFarState")
        return bestSoFarState.geodeCount + bestSoFarState.geodeRobots
    }
}

private data class MiningSimulationState(
    val oreRobots: Int,
    val clayRobots: Int,
    val obsidianRobots: Int,
    val geodeRobots: Int,
    val oreCount: Int,
    val clayCount: Int,
    val obsidianCount: Int,
    val geodeCount: Int,
    val currentMinute: Int,
) : Comparable<MiningSimulationState> {
    fun neighbors(blueprint: Blueprint): List<MiningSimulationState> {
        // this things can I construct
        val robotConstructionOptions = Resource.values().filter {
            canMakeResourceRobot(blueprint, it)
        }

        // how many resources will I get this turn
        val newOreCount = oreCount + oreRobots
        val newClayCount = clayCount + clayRobots
        val newObsidianCount = obsidianCount + obsidianRobots
        val newGeodeCount = geodeCount + geodeRobots

        // the best option is to make a geode robot if possible
        if (Resource.GEODE in robotConstructionOptions) {
            return listOf(
                MiningSimulationState(
                    oreRobots = oreRobots,
                    clayRobots = clayRobots,
                    obsidianRobots = obsidianRobots,
                    geodeRobots = geodeRobots + 1,
                    oreCount = newOreCount - blueprint.geodeRobotOreCost,
                    clayCount = newClayCount,
                    obsidianCount = newObsidianCount - blueprint.geodeRobotObsidianCost,
                    geodeCount = newGeodeCount,
                    currentMinute = currentMinute + 1,
                ),
            )
        }

        val neighbors = arrayListOf<MiningSimulationState>()

        // no need creating more robots than we can consume ore in a turn
        if (Resource.ORE in robotConstructionOptions && oreRobots < blueprint.maxOreCost) {
            neighbors.add(
                MiningSimulationState(
                    oreRobots = oreRobots + 1,
                    clayRobots = clayRobots,
                    obsidianRobots = obsidianRobots,
                    geodeRobots = geodeRobots,
                    oreCount = newOreCount - blueprint.oreRobotOreCost,
                    clayCount = newClayCount,
                    obsidianCount = newObsidianCount,
                    geodeCount = newGeodeCount,
                    currentMinute = currentMinute + 1,
                ),
            )
        }

        // only obsidian robots use clay as a material
        if (Resource.CLAY in robotConstructionOptions && clayRobots < blueprint.obsidianRobotClayCost) {
            neighbors.add(
                MiningSimulationState(
                    oreRobots = oreRobots,
                    clayRobots = clayRobots + 1,
                    obsidianRobots = obsidianRobots,
                    geodeRobots = geodeRobots,
                    oreCount = newOreCount - blueprint.clayRobotOreCost,
                    clayCount = newClayCount,
                    obsidianCount = newObsidianCount,
                    geodeCount = newGeodeCount,
                    currentMinute = currentMinute + 1,
                ),
            )
        }

        // only geode robots use obsidian
        if (Resource.OBSIDIAN in robotConstructionOptions && obsidianRobots < blueprint.geodeRobotObsidianCost) {
            neighbors.add(
                MiningSimulationState(
                    oreRobots = oreRobots,
                    clayRobots = clayRobots,
                    obsidianRobots = obsidianRobots + 1,
                    geodeRobots = geodeRobots,
                    oreCount = newOreCount - blueprint.obsidianRobotOreCost,
                    clayCount = newClayCount - blueprint.obsidianRobotClayCost,
                    obsidianCount = newObsidianCount,
                    geodeCount = newGeodeCount,
                    currentMinute = currentMinute + 1,
                ),
            )
        }

        // final option: don't make any new robots
        // there's cases where you will need to make sure to hold off on creating a robot;
        // if you can save up for a better robot
        neighbors.add(
            MiningSimulationState(
                oreRobots = oreRobots,
                clayRobots = clayRobots,
                obsidianRobots = obsidianRobots,
                geodeRobots = geodeRobots,
                oreCount = newOreCount,
                clayCount = newClayCount,
                obsidianCount = newObsidianCount,
                geodeCount = newGeodeCount,
                currentMinute = currentMinute + 1,
            ),
        )

        return neighbors
    }

    fun canMakeResourceRobot(blueprint: Blueprint, resource: Resource): Boolean {
        return when (resource) {
            Resource.ORE -> this.oreCount >= blueprint.oreRobotOreCost
            Resource.CLAY -> this.oreCount >= blueprint.clayRobotOreCost
            Resource.OBSIDIAN -> this.oreCount >= blueprint.obsidianRobotOreCost && this.clayCount >= blueprint.obsidianRobotClayCost
            Resource.GEODE -> this.oreCount >= blueprint.geodeRobotOreCost && this.obsidianCount >= blueprint.geodeRobotObsidianCost
        }
    }

    override fun compareTo(other: MiningSimulationState): Int {
        return this.toString().compareTo(other.toString())
    }
}

enum class Resource {
    ORE, CLAY, OBSIDIAN, GEODE
}
