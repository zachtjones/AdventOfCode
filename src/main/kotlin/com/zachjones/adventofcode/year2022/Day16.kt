package com.zachjones.adventofcode.year2022

import kotlin.collections.HashMap

private const val MAX_MINUTE: Int = 30

private typealias SmartBitSet = Int
fun SmartBitSet.isSet(position: Int): Boolean {
    return (this and (1 shl position)) != 0
}

fun SmartBitSet.set(position: Int): SmartBitSet {
    return this or (1 shl position)
}

// why oh why did I get a dynamic programming problem today
// big sad

// optimization 1: Use HashMap; cache repeated calculations -- keys are [place: Valve,open:Set<Valve>,currentMinute:Int]
// optimization 2: Use "smart valve" data class -- Int-based math based on the index of the valve
// optimization 3: Caching the score per open valve set in a HashMap
// optimization 4: Use java.util.BitSet rather than Set<SmartValve>

// optimization 5: create an Array to map from ValveIndex -> Open-ableValveIndex
// optimization 6: pre-compute flow @ openAble indexes small array
// -- this solved for part 1, ran for 1602ms
// optimization 7: If all open-able valves are open, just sit still
// optimization 8: Move from BitSet to Int (using bit shifts) directly & index the arrays
// optimization 9: Score cache is an array
// optimization 10: moves cache is an array (2^^32 elements)
// optimization 11: we actually can't do that since indexes are Int and we can't do UInt in JVM
// -- so I made it an array of everything but the bit mask, the mask is now separate

// Note: Part 2 does not work, I get the wrong answer

fun main() {
    val input = FileReader2022.readFile(day = 16, example = false)
    val valves = input.split('\n')
        .mapIndexed { index, it -> Valve(index, it) }.associateBy { it.index }

    // smart valves is much more memory efficient
    val smartValves = Array(size = valves.size) { index ->
        val dumbValve = valves[index]!!
        SmartValve(
            flow = dumbValve.flow,
            neighbors = dumbValve.leadingTo.map { name ->
                valves.values.first { it.name == name }.index
            }.toIntArray(),
        )
    }
    val startSpot: Int = valves.values.first { it.name == "AA" }.index

    println("Parsed input")
    var start = System.currentTimeMillis()

    println("Part 1: max by opening valves")
    val score = Solver(smartValves).score(
        startingSpot = startSpot,
        currentMinute = 1,
    )

    println("Score is $score")
    println("Took ${System.currentTimeMillis() - start}ms")

    println("\nPart 2: max by opening valves with elephant helper")
    start = System.currentTimeMillis()
    val score2 = Solver(smartValves).scorePart2(
        startingSpot = startSpot,
        currentMinute = 5,
    )
    println("Score is $score2")
    println("Took ${System.currentTimeMillis() - start}ms")
}

private class Solver(val valves: Array<SmartValve>) {

    /**
     * mapping from index in valves array to the index in the
     * more compact array of ones that can be opened
     *
     * -1 means this valve is not able to be opened
     */
    private val openAbleIndexMap: IntArray = IntArray(valves.size) { -1 }
    private val openAbleValveCount: Int

    /** reverse mapping from openAbleIndexMap */
    private val openAbleValvesToValveIndex: IntArray

    /** openValve index -> flow, speeds up openFlow calculation for rounds */
    private val openAbleValveFlow: IntArray

    private val cache = HashMap<CacheKey, Int>()

    // cache for moves for part 2
    private val cache2: Array<HashMap<Int, Int>>
    private val smartBitSetAllSet: SmartBitSet
    private val scoresPerRoundCache: IntArray

    init {
        // fill in the openAbleIndexMap with the calculation
        var openCount = 0
        for (i in valves.indices) {
            if (valves[i].flow != 0) {
                openAbleIndexMap[i] = openCount
                openCount++
            }
        }
        openAbleValveCount = openCount
        openAbleValvesToValveIndex = IntArray(openAbleValveCount)
        for (i in openAbleIndexMap.indices) {
            val openValveIndex = openAbleIndexMap[i]
            if (openValveIndex != -1) {
                openAbleValvesToValveIndex[openValveIndex] = i
            }
        }

        // build openable valve flow
        openAbleValveFlow = IntArray(openAbleValveCount)
        for (i in openAbleValveFlow.indices) {
            val valveIndex = openAbleValvesToValveIndex[i]
            openAbleValveFlow[i] = valves[valveIndex].flow
        }

        println("There are $openAbleValveCount valves that can be opened, compared to ${valves.size} in total")
        require(openAbleValveCount < 64) {
            "This implementation requires all openAbleValves to fit into a bit masked long number"
        }

        var tempSmartBitSet = 0
        for (i in 0 until openAbleValveCount) {
            tempSmartBitSet = tempSmartBitSet.set(i)
        }
        smartBitSetAllSet = tempSmartBitSet

        // start with -1 as all values
        scoresPerRoundCache = IntArray(smartBitSetAllSet + 1) { -1 }

        require(MAX_MINUTE < 32) {
            "Algorithm has MAX MINUTE < 32 so it fits into 5 bits"
        }
        require(valves.size < 64) {
            "Algorithm has VALVES.size < 64 so it fits into 6 bits"
        }
        require(openAbleValveCount <= 15) {
            "Algorithm supports max 15 valves that have positive flow when opened"
        }
        // need all 32 bits max to store our cache
        // this number is the max the JVM supports; hopefully we don't need it all
        // make sure to run with extra ram
        cache2 = Array(1 shl 17) { HashMap() }
    }

    fun scorePerRound(openValves: SmartBitSet): Int {
        val lookup = scoresPerRoundCache[openValves]
        if (lookup != -1) {
            return lookup
        }
        var sum = 0
        for (i in 0 until openAbleValveCount) {
            if (openValves.isSet(i)) {
                sum += openAbleValveFlow[i]
            }
        }
        scoresPerRoundCache[openValves] = sum
        return sum
    }

    fun score(startingSpot: Int, currentMinute: Int): Int {
        return score(
            currentMinute = currentMinute,
            currentSpot = startingSpot,
            openValves = 0,
        )
    }

    private fun score(
        currentMinute: Int,
        currentSpot: Int, // index into array
        openValves: SmartBitSet,
    ): Int {
        // see if we've already computed this
        val key = CacheKey(
            currentMinute = currentMinute,
            currentSpot = currentSpot,
            openValves = openValves,
        )
        return cache.getOrPut(key) {
            val scoreThisRound: Int = scorePerRound(openValves)

            // base case, time is up
            if (currentMinute == MAX_MINUTE) return scoreThisRound

            // 2nd base case -- all valves are open -- no need to check other options
            if (openValves.allAreSet()) {
                return@getOrPut scoreThisRound + score(
                    currentMinute = currentMinute + 1,
                    currentSpot = currentSpot,
                    openValves = openValves,
                )
            }

            val options = mutableListOf<Int>()

            val currentOpenValveSpot = openAbleIndexMap[currentSpot]

            // open this valve
            if (currentOpenValveSpot != -1 && !openValves.isSet(currentOpenValveSpot)) {
                options.add(
                    score(
                        currentMinute = currentMinute + 1,
                        currentSpot = currentSpot,
                        openValves = openValves.set(currentOpenValveSpot),
                    ),
                )
            }

            // OR:
            // travel to each other spot
            val valvesToGoTo = valves[currentSpot].neighbors
            for (newValve in valvesToGoTo) {
                options.add(
                    score(
                        currentMinute = currentMinute + 1,
                        currentSpot = newValve,
                        openValves = openValves,
                    ),
                )
            }

            return@getOrPut scoreThisRound + options.max()
        }
    }

    fun scorePart2(startingSpot: Int, currentMinute: Int): Int {
        return scorePart2(
            currentMinute = currentMinute,
            currentSpot = startingSpot,
            elephantSpot = startingSpot,
            openValves = 0,
        )
    }

    private fun scorePart2(
        currentMinute: Int,
        currentSpot: Int,
        elephantSpot: Int,
        openValves: SmartBitSet,
    ): Int {
        // see if we've already computed this
        val key = CacheKeyPart2(
            currentMinute = currentMinute,
            currentSpot = currentSpot,
            elephantSpot = elephantSpot,
            openValves = openValves,
        ).toInt()

        val cachedValue = cache2[key][openValves]
        if (cachedValue != null) {
            return cachedValue
        }
        val scoreThisRound: Int = scorePerRound(openValves)

        // base case, time is up
        if (currentMinute == MAX_MINUTE) return scoreThisRound

        val currentOpenValveYouSpot = openAbleIndexMap[currentSpot]
        val currentOpenValveElephantSpot = openAbleIndexMap[elephantSpot]

        // optimization -- if all valves that can be opened are opened, just chill
        if (openValves.allAreSet()) {
            return scoreThisRound + scorePart2(
                currentMinute = currentMinute + 1,
                currentSpot = currentSpot,
                elephantSpot = elephantSpot,
                openValves = openValves,
            )
        }

        // new options:
        // 1. you both stay + open valves
        // 2. you both move
        // 3. you stay, elephant moves
        // 4. you move, elephant stays

        // there's never a reason to stay put (unless all valves are open)

        val options = mutableListOf<Int>()

        // option 1: you both stay + open valves
        // only applicable if you and elephant are in different spots
        // and both valves are open-able
        if (currentOpenValveYouSpot != -1 &&
            currentOpenValveElephantSpot != -1 &&
            currentOpenValveYouSpot != currentOpenValveElephantSpot
        ) {
            options.add(
                scorePart2(
                    currentMinute = currentMinute + 1,
                    currentSpot = currentSpot,
                    elephantSpot = elephantSpot,
                    openValves = openValves
                        .set(currentOpenValveElephantSpot)
                        .set(currentOpenValveYouSpot),
                ),
            )
        }

        // option 2. you both move
        val valvesYouCanGoTo = valves[currentSpot].neighbors
        val valvesElephantCanGoTo = valves[elephantSpot].neighbors
        for (yourNewSpot in valvesYouCanGoTo) {
            for (elephantNewSpot in valvesElephantCanGoTo) {
                options.add(
                    scorePart2(
                        currentMinute = currentMinute + 1,
                        currentSpot = yourNewSpot,
                        elephantSpot = elephantNewSpot,
                        openValves = openValves,
                    ),
                )
            }
        }

        // option 3. you open valve, elephant moves
        if (currentOpenValveYouSpot != -1) {
            for (elephantNewSpot in valvesElephantCanGoTo) {
                options.add(
                    scorePart2(
                        currentMinute = currentMinute + 1,
                        currentSpot = currentSpot,
                        elephantSpot = elephantNewSpot,
                        openValves = openValves.set(currentOpenValveYouSpot),
                    ),
                )
            }
        }

        // option 4. you move, elephant opens valve
        if (currentOpenValveElephantSpot != -1) {
            for (yourNewSpot in valvesYouCanGoTo) {
                options.add(
                    scorePart2(
                        currentMinute = currentMinute + 1,
                        currentSpot = yourNewSpot,
                        elephantSpot = elephantSpot,
                        openValves = openValves.set(currentOpenValveElephantSpot),
                    ),
                )
            }
        }

        val newScore = scoreThisRound + options.max()
        cache2[key][openValves] = newScore
        return newScore
    }

    private fun SmartBitSet.allAreSet(): Boolean = this == smartBitSetAllSet
}

private data class CacheKey(
    val currentMinute: Int,
    val currentSpot: Int,
    val openValves: SmartBitSet,
)
private data class CacheKeyPart2(
    val currentMinute: Int,
    val currentSpot: Int,
    val elephantSpot: Int,
    val openValves: SmartBitSet,
) {
    fun toInt(): Int {
        // bits are as follows
        // currentMinute[5] | spot[6] | slephant spot[6]
        return (currentMinute shl 11) or (currentSpot shl 6) or elephantSpot
    }
}

private class Valve(
    val index: Int,
    input: String,
) {
    val name: String
    val flow: Int
    val leadingTo: List<String>

    init {
        val find = Regex("Valve (.*) has flow rate=(.*); tunnel[s]* lead[s]* to valve[s]* (.*)")
            .find(input) ?: throw IllegalArgumentException(input)
        val groups = find.groupValues
        name = groups[1]
        flow = groups[2].toInt()
        leadingTo = groups[3].split(Regex(", "))
    }
}
private class SmartValve(
    val flow: Int,
    val neighbors: IntArray,
)
