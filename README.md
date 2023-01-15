# AdventOfCode
My solutions to the 25 day coding challenges on [AdventOfCode](https://adventofcode.com/)

I started this challenge for the 2022 challenge, and will contribute subsequent years.

My personal goal was to be able to solve the challenges with minimal help needed from the internet.

## Structure
In this repository I have the solutions to each day, files are named. Day<X>, and are in a package by the year
(see src/main/kotlin/com.zachjones.year<2022>)

I have also included the inputs for each day under a folder src/main/resources/<year>, named
input-<day>.txt and input-<example>.txt.

These programs are designed to work for both part 1 and part 2 of each day's challenge, as well as
with the example smaller problems they give.

If you're new to the challenge, you may notice that the input's for the day are unique to each person,
so everyone will have slightly different answers.

## Usage

To invoke a single day's challenges, just run the main method in that class. It will print out the solutions.

(still working on this) To run all days, you can run the unit tests using maven.
```shell
mvn clean test
```

Note that some days can take up to a minute, since some of these are very large input sets and computationally
complicated.

## Contributing
Please don't contribute code to this repo, however feel free to read/copy this code to help your understanding
of my solutions and my thought process to how I handle these coding challenges.