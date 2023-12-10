package com.zachjones.adventofcode.year2022

import com.zachjones.adventofcode.FileReader

object FileReader2022 {

    private val mainReader = FileReader(2022)
    fun readFile(day: Int, example: Boolean = false): String = mainReader.readFile(day, example)
}
