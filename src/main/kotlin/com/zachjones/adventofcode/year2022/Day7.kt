package com.zachjones.adventofcode.year2022

import java.util.*

class Day7(isExample: Boolean): BaseChallenge2022(day = 7, isExample) {

    private val lines = LinkedList(inputContent.split('\n'))
    private val topLevelDirectory = Directory("/", parent = null)


    init {
        // first line is 'cd /'
        lines.removeFirst()
        var currentDirectory = topLevelDirectory

        fun populateCurrentDirectory() {
            while(lines.isNotEmpty() && !lines.first().startsWith("$")) {
                currentDirectory.addItemFromString(lines.removeFirst())
            }
        }

        while(lines.isNotEmpty()) {
            val instruction = lines.removeFirst()
            require(instruction.startsWith("$")) {
                "Incorrect state, instruction should start with $ but saw $instruction"
            }
            if (instruction == "$ cd ..") {
                currentDirectory = currentDirectory.parent!!
            } else if (instruction.startsWith("$ cd")) {
                val newDirectoryName = instruction.split(' ')[2]
                currentDirectory = currentDirectory.items
                    .filterIsInstance<Directory>()
                    .firstOrNull { it.name == newDirectoryName }
                    ?: throw IllegalArgumentException("No directory named $newDirectoryName")
            }
            if (instruction == "$ ls") {
                populateCurrentDirectory()
            }
        }

        println("Directory tree parsed:")
        println(topLevelDirectory)
    }

    override fun solvePart1(): String {
        // part 1 -- all directories at most 100000 size
        val smallerDirectories = topLevelDirectory.childDirectoriesLessThan(100_000)
        //print("Smaller directories are: ")
        //println(smallerDirectories.joinToString { it.name })

        return smallerDirectories.sumOf { it.size() }.toString()
    }

    override fun solvePart2(): String {
        // part 2 - delete the right directory that frees up the space required
        val freeSpaceNeeded = 30_000_000
        val volumeSize = 70_000_000
        val spaceNeededToDelete = topLevelDirectory.size() - (volumeSize - freeSpaceNeeded)
        //println("\nNeed to delete $spaceNeededToDelete")
        val directoryNeeded = topLevelDirectory.smallestDirectoryBiggerThan(spaceNeededToDelete)!!
        //println("Smallest directory needed ${directoryNeeded.name} for size ${directoryNeeded.size()}")
        return directoryNeeded.size().toString()
    }

}

fun main() {
    val day = Day7(isExample = false)
    println("Part 1: ${day.solvePart1()}")
    println("Part 2: ${day.solvePart2()}")
}

private interface ItemOnDisk {
    fun size(): Int
}

private data class File(
    private val name: String,
    private val size: Int
    ): ItemOnDisk {

    override fun size(): Int = size
}

private data class Directory(
    val name: String,
    val parent: Directory?,
    val items: MutableList<ItemOnDisk> = arrayListOf()
): ItemOnDisk {

    override fun size(): Int = items.sumOf { it.size() }

    fun addItemFromString(item: String) {
        if (item.startsWith("dir")) {
            val name = item.split(' ')[1]
            this.items.add(Directory(name, parent = this))
        } else {
            val split = item.split(' ')
            val size = split[0].toInt()
            val name = split[1]
            this.items.add(File(name, size))
        }
    }

    override fun toString(): String {
        return "{Directory name=$name, items=$items}"
    }

    fun childDirectoriesLessThan(maxSize: Int): List<Directory> {
        val list = mutableListOf<Directory>()
        for(childDirectory in items.filterIsInstance<Directory>()) {
            if (childDirectory.size() <= maxSize) {
                list.add(childDirectory)
            }
            list.addAll(childDirectory.childDirectoriesLessThan(maxSize))
        }
        return list
    }

    fun smallestDirectoryBiggerThan(minSize: Int): Directory? {
        if (this.size() < minSize) return null

        // no children are big enough, but this is
        if (this.size() > minSize && items.filterIsInstance<Directory>().none {
            it.size() >= minSize
        }) {
            return this
        }

        return items.filterIsInstance<Directory>()
            .mapNotNull { it.smallestDirectoryBiggerThan(minSize) }
            .minByOrNull { it.size() }
    }
}