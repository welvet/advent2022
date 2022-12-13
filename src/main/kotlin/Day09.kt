private val GRID = 12

object Day09 {

    data class Knot(
        var x: Int = GRID / 2,
        var y: Int = GRID / 2
    ) {
        val visited = mutableSetOf<Pair<Int, Int>>()

        fun move(dir: String) {
            when (dir) {
                "U" -> y--
                "D" -> y++
                "L" -> x--
                "R" -> x++
            }
        }

        fun follow(head: Knot) {
            val xDist = Math.abs(head.x - x)
            val yDist = Math.abs(head.y - y)

            var farX = xDist > 1
            var farY = yDist > 1
            if ((farX || farY) && (xDist >= 1 && yDist >= 1)) {
                farX = true
                farY = true
            }

            if (farX) {
                if (head.x < x) {
                    x--
                } else {
                    x++
                }
            }
            if (farY) {
                if (head.y < y) {
                    y--
                } else {
                    y++
                }
            }

            visited.add(Pair(x, y))
        }
    }





}

fun main(args: Array<String>) {
    val lines = "day09.txt"
        .readFile()
        .lines()
        .map { it.trim() }
        .filter { it.isNotBlank() }

    val head = Day09.Knot()
    val tail = Day09.Knot()

    lines.forEach {
        val (dir, count) = it.split(" ")
        repeat(count.toInt()) {
            head.move(dir)
            tail.follow(head)
        }
    }

    println(tail.visited.size)
}
