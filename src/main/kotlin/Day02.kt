import Day02.Gesture.*

object Day02 {

    val winnings = mapOf(
        ROCK to SCISSORS,
        PAPER to ROCK,
        SCISSORS to PAPER
    )

    val firstMap = mapOf(
        "A" to ROCK,
        "B" to PAPER,
        "C" to SCISSORS
    )

    val secondMap = mapOf(
        "X" to ROCK,
        "Y" to PAPER,
        "Z" to SCISSORS
    )

    enum class Gesture(private val gestureScore: Int) {
        ROCK(1), PAPER(2), SCISSORS(3);

        fun getScore(other: Gesture): Int {
            val gameScore = if (this == other) {
                3
            } else if (winnings[this] == other) {
                6
            } else {
                0
            }

            return gameScore + gestureScore
        }
    }
}

fun main(args: Array<String>) {
    val lines = "day02.txt".readFile().lines()

    var max = 0
    lines.forEach {
        if (it.isBlank()) {
            return@forEach
        }

        val pair = it.trim().split(" ")
        val opponent = Day02.firstMap[pair[0]]!!
        val you = Day02.secondMap[pair[1]]!!

        max += you.getScore(opponent)
    }

    println("Result: $max")
}
