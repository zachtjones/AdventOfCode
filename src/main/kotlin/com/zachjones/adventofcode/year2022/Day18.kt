package com.zachjones.adventofcode.year2022

import java.util.TreeSet

fun main() {
    val input = FileReader.readFile(day = 18, example = false)
    val cubes = input.split('\n').map { line ->
        val split = line.split(',').map { it.toInt() }
        Cube(split[0], split[1], split[2])
    }.toSet()

    println("Part 1: Surface area of the blob")
    println("Surface Area = ${cubes.sumOf { it.exposedFacesPart1(cubes) }}")

    println("Part 2: Don't count internal air pockets")
    println("External surface area = ${cubes.sumOf { it.exposedFacesPart2(cubes) }}")
}

data class Cube(val x: Int, val y: Int, val z: Int) : Comparable<Cube> {
    private fun neighbors() = listOf(
        Cube(x - 1, y, z),
        Cube(x + 1, y, z),
        Cube(x, y - 1, z),
        Cube(x, y + 1, z),
        Cube(x, y, z - 1),
        Cube(x, y, z + 1),
    )

    fun exposedFacesPart1(cubes: Set<Cube>): Int {
        return neighbors().count { it !in cubes }
    }

    fun exposedFacesPart2(cubes: Set<Cube>): Int {
        return neighbors().count { it !in cubes && !it.isInternal(cubes) }
    }

    private fun isInternal(cubes: Set<Cube>): Boolean {
        // notes
        // this could be optimized; when we do the flood, we can figure out all the internal
        // sides that are in this 'bubble' on the inside

        // also note this algorithm does not work for droplets that
        // have more air inside them than the volume of stuff they are made of
        // aka; if this droplet was >50% air then this algorithm does not work
        // we would need a higher upper bound -- we would need to properly count the
        // volume this object takes up, which is the cubes.size + internal volume

        val dropletVolume = cubes.size

        // flood fill algorithm; if we can fill > droplet's volume, we know that this is external
        val flood = hashSetOf<Cube>()
        val queue = TreeSet<Cube>()
        queue.add(this)
        while (queue.isNotEmpty()) {
            val current = queue.first()
            queue.remove(current)

            // fill neighbors that are air
            val neighbors = current.neighbors().filter { it !in flood && it !in cubes }
            queue.addAll(neighbors)
            flood.addAll(neighbors)

            // the air can escape outside the shape
            if (flood.size > dropletVolume) {
                return false
            }
        }
        // the queue ran out of elements - aka there is not a connection from this air to the outside
        return true
    }

    override fun compareTo(other: Cube): Int {
        return "$x,$y,$z".compareTo("${other.x},${other.y},${other.z}")
    }
}
