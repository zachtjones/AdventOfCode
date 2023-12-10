package com.zachjones.adventofcode.year2022

import com.zachjones.adventofcode.BaseChallenge

abstract class BaseChallenge2022(day: Int, isExample: Boolean) : BaseChallenge(day, isExample) {

    /**
     * The loaded input's content, based on the day and example/not.
     * This will contain the '\n' characters for newlines
     */
    override val inputContent: String = FileReader2022.readFile(day, isExample)
}
