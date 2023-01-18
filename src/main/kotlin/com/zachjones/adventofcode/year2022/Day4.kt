package com.zachjones.adventofcode.year2022

private typealias Job = IntRange // range of groups
private typealias Buddies = Pair<Job, Job> // Buddies are elves that work in pairs

class Day4(isExample: Boolean): BaseChallenge2022(day = 4, isExample) {

    private val lines = inputContent.split('\n')

    // parse the input
    private val allJobs: List<Buddies> = lines.map { line ->
        val assignments: List<Job> = line.split(',').map { part ->
            val range = part.split('-')
            range[0].toInt()..range[1].toInt()
        }
        val assignmentOne = assignments[0]
        val assignmentTwo = assignments[1]
        return@map assignmentOne to assignmentTwo
    }

    override fun solvePart1(): String {
        // part 1 - fully contain
        val jobsFullyContain = allJobs.filter { buddies -> buddies.first.fullyContains(buddies.second) || buddies.second.fullyContains(buddies.first) }
        println("There are ${jobsFullyContain.size} jobs where one is fully contained in the other")

        return jobsFullyContain.size.toString()
    }

    override fun solvePart2(): String {
        // part 2 - is there some overlap?
        val jobsWithOverlap = allJobs.filter { buddies -> buddies.first.overlaps(buddies.second) || buddies.second.overlaps(buddies.first) }
        println("There are ${jobsWithOverlap.size} jobs where there is overlap")

        return jobsWithOverlap.size.toString()
    }

}

fun main() {
    val day = Day4(isExample = false)
    println("Part 1: ${day.solvePart1()}")
    println("Part 2: ${day.solvePart2()}")
}

fun Job.fullyContains(other: Job): Boolean {
    return this.contains(other.min()) && this.contains(other.max())
}

fun Job.overlaps(other: Job): Boolean {
    return this.contains(other.min()) || this.contains(other.max())
}