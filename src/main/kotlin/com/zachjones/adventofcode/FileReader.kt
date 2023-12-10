package com.zachjones.adventofcode

class FileReader(private val year: Int) {
    /**
     * Reads the text file for the day, returning it as a giant string.
     * @param example if you wish to read the example file instead
     */
    fun readFile(day: Int, example: Boolean = false): String {
        val name = if (example) {
            "$year/input-example-$day.txt"
        } else {
            "$year/input-$day.txt"
        }
        return this::class.java.classLoader.getResource(name)?.readText()
            ?: throw IllegalArgumentException("File $name does not exist in the resources")
    }
}
