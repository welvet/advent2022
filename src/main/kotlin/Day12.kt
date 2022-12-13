import java.util.*

data class Point(
    val x: Int,
    val y: Int,
    val steps: Int
)


fun main(args: Array<String>) {
    val lines = "day12.txt"
        .readFile()
        .lines()
        .map { it.trim() }
        .filter { it.isNotBlank() }

    val X = Array(lines.size) { IntArray(lines[0].length) }

    val starts = mutableListOf<Pair<Int, Int>>()
    var end: Pair<Int, Int>? = null

    lines.forEachIndexed { i, s ->
        s.toCharArray().forEachIndexed { j, c ->
            when (c) {
                'S' -> {
                    X[i][j] = 0
                    starts.add(Pair(i, j))
                }

                'E' -> {
                    end = Pair(i, j)
                    X[i][j] = 'z' - 'a'
                }

                else -> {
                    X[i][j] = c - 'a'
                    if (c - 'a' == 0) {
                        starts.add(Pair(i, j))
                    }
                }
            }
        }
    }

    starts.map { start ->
        val Y = Array(lines.size) { IntArray(lines[0].length) { Int.MAX_VALUE } }

        fun elevation(x: Int, y: Int, currentElevation: Int): Boolean {
            if (x < 0 || y < 0) {
                return false
            }

            if (x >= X.size || y >= X[0].size) {
                return false
            }

            return currentElevation + 1 >= X[x][y]
        }

        fun Point.handle(xDiff: Int, yDiff: Int): Point? {
            val currentElevation = X[x][y]
            val x1 = x + xDiff
            val y1 = y + yDiff

            if (elevation(x1, y1, currentElevation)) {
                if (steps + 1 < Y[x1][y1]) {
                    Y[x1][y1] = steps + 1
                    return Point(x1, y1, steps + 1)
                }
            }

            return null
        }

        val queue = PriorityQueue(Comparator.comparing(Point::steps))
        queue.add(Point(start!!.first, start!!.second, 0))

        while (queue.isNotEmpty()) {
            val next = queue.poll()
            next.handle(1, 0)?.let { queue.add(it) }
            next.handle(-1, 0)?.let { queue.add(it) }
            next.handle(0, 1)?.let { queue.add(it) }
            next.handle(0, -1)?.let { queue.add(it) }
        }

        Y[end!!.first][end!!.second]
    }
        .min()
        .let { println(it) }
}
