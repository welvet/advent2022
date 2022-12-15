import kotlin.math.max
import kotlin.math.min

object Day14 {
    val X = Array(1024) { IntArray(1024) }

    data class Point(
        val x: Int,
        val y: Int
    )

    fun parsePoint(s: String): Point {
        val (x, y) = s.split(",").map { it.toInt() }
        return Point(x, y)
    }

    fun printRange(x1: Int, x2: Int, y1: Int, y2: Int) {
        for (y in y1..y2) {
            for (x in x1..x2) {
                when (X[y][x]) {
                    0 -> print('.')
                    1 -> print('#')
                    2 -> print('o')
                }
            }
            println()
        }
    }

    fun run() {
        val lines = "day14.txt"
            .readFile()
            .lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val points = lines
            .map {
                it.split(" -> ").map(Day14::parsePoint)
            }
            .flatMap { points ->
                var p1: Point?
                var p2: Point? = points[0]

                (1 until points.size).map { i ->
                    p1 = p2
                    p2 = points[i]

                    Pair(p1!!, p2!!)
                }
            }
        println(points)

        points
            .forEach { (p1, p2) ->
                if (p1.y == p2.y) {
                    for (x in min(p1.x, p2.x)..max(p1.x, p2.x)) {
                        X[p1.y][x] = 1
                    }
                } else {
                    for (y in min(p1.y, p2.y)..max(p1.y, p2.y)) {
                        X[y][p1.x] = 1
                    }
                }
            }

        val lowestY = points.flatMap { it.toList() }.maxOf { it.y }

        for (x in 0 until X[0].size) {
            X[lowestY + 2][x] = 1
        }

        var totalFixed = 0
        var x = 500
        var y = 0
        while (true) {
            if (X[y + 1][x] == 0) {
                X[y][x] = 0
                X[++y][x] = 2
            } else if (X[y + 1][x - 1] == 0) {
                X[y][x] = 0
                X[++y][--x] = 2
            } else if (X[y + 1][x + 1] == 0) {
                X[y][x] = 0
                X[++y][++x] = 2
            } else {
                totalFixed++
                if (x == 500 && y == 0) {
                    break
                }
                x = 500
                y = 0
            }
        }

        printRange(300, 700, 0, lowestY + 2)

        println("Total fixed: $totalFixed")
    }

}

fun main() {
    Day14.run()
}
