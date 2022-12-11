import Day02B.Gesture.*
import Day02B.Target.*

object Day02B {

    val winnings = mapOf(
        ROCK to PAPER,
        PAPER to SCISSORS,
        SCISSORS to ROCK
    )

    val loosings = mapOf(
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
        "X" to LOOSE,
        "Y" to DRAW,
        "Z" to WIN
    )

    enum class Target(val score: Int, val getMine: (Gesture) -> Gesture?) {
        WIN(6, winnings::get), LOOSE(0, loosings::get), DRAW(3, { it });
    }

    enum class Gesture(val gestureScore: Int) {
        ROCK(1), PAPER(2), SCISSORS(3);
    }
}

fun main(args: Array<String>) {
    val lines = "day02.txt".readFile().lines()


    val max = lines
        .filter { it.isNotBlank() }
        .filter { !it.trim().startsWith("#") }
        .sumOf {
            val pair = it.trim().split(" ")
            val opponent = Day02B.firstMap[pair[0]]!!
            val target = Day02B.secondMap[pair[1]]!!
            target.getMine(opponent)!!.gestureScore + target.score
        }

    println("Result: $max")
}
