import java.util.*

fun main(args: Array<String>) {
    val lines = "day01.txt".readFile().lines()

    val pq = PriorityQueue<Int>(Collections.reverseOrder())
    var current = 0
    lines.forEach {
        if (it.isBlank()) {
            pq.add(current)

            current = 0
        } else {
            current += it.trim().toInt()
        }
    }

    val result = (0..2).sumOf {
        pq.poll()
    }

    println("Result: $result")
}
