package com.zachjones.adventofcode

class FileReader(private val year: Int) {
    /**
     * Reads the text file for the day, returning it as a giant string.
     * @param example if you wish to read the example file instead
     */
    fun readFile(day: Int, example: Boolean = false, separatePart2: Boolean = false): String {
        val part2 = if (separatePart2) "-part2" else ""
        val name = if (example) {
            "$year/input-example-$day$part2.txt"
        } else {
            "$year/input-$day$part2.txt"
        }
        return this::class.java.classLoader.getResource(name)?.readText()
            ?: throw IllegalArgumentException("File $name does not exist in the resources")
    }
}
