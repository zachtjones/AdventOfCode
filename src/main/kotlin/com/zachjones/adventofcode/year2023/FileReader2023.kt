package com.zachjones.adventofcode.year2023

import com.zachjones.adventofcode.FileReader

object FileReader2023 {

    private val mainReader = FileReader(2023)
    fun readFile(day: Int, example: Boolean, separatePart2: Boolean = false): String = mainReader.readFile(day, example, separatePart2)
}
