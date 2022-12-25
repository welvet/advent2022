import java.util.LinkedHashSet
import java.util.PriorityQueue

object Day24 {

    data class Point(val x: Int, val y: Int) {
        override fun toString(): String {
            return "[x=$x y=$y]"
        }
    }

    fun p(x: Int, y: Int): Point {
        return Point(x, y)
    }

    enum class Direction(val dp: Point, val c: Char) {
        UP(p(0, -1), '^'), RIGHT(p(1, 0), '>'), DOWN(p(0, 1), 'v'), LEFT(p(-1, 0), '<');
    }

    interface Obstacle {
        val p: Point

        fun move(x1: Int, x2: Int, y1: Int, y2: Int): Obstacle
    }

    data class Blizzard(override val p: Point, val direction: Direction) : Obstacle {
        override fun move(x1: Int, x2: Int, y1: Int, y2: Int): Obstacle {
            var newX = p.x + direction.dp.x
            var newY = p.y + direction.dp.y

            if (newX == x1) {
                newX = x2 - 1
            } else if (newX == x2) {
                newX = x1 + 1
            }

            if (newY == y1) {
                newY = y2 - 1
            } else if (newY == y2) {
                newY = y1 + 1
            }

            return Blizzard(p(newX, newY), direction)
        }

        override fun toString(): String {
            return direction.c.toString()
        }
    }

    data class Wall(override val p: Point) : Obstacle {
        override fun move(x1: Int, x2: Int, y1: Int, y2: Int): Obstacle {
            return this
        }

        override fun toString(): String {
            return "#"
        }
    }

    data class Space(
        val obstacles: Set<Obstacle>
    ) {
        fun printMe(mx: Int, my: Int, ex: Int, ey: Int) {
            val points = obstacles.groupBy { it.p }

            for (y in 0 until my) {
                for (x in 0 until mx) {
                    val objects = points[p(x, y)]

                    if (x == ex && y == ey) {
                        print('E')
                    } else if (objects == null) {
                        print('.')
                    } else if (objects.size == 1) {
                        print(objects[0].toString())
                    } else {
                        print(objects.size)
                    }
                }
                println()
            }
        }
    }

    data class Expedition(
        val p: Point,
        val roundTurn: Int,
        val turn: Int,
        var prev: Expedition? = null
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Expedition

            if (p != other.p) return false
            if (roundTurn != other.roundTurn) return false

            return true
        }

        override fun hashCode(): Int {
            var result = p.hashCode()
            result = 31 * result + roundTurn
            return result
        }
    }

    fun run() {
        val lines = "day24.txt"
            .readFile()
            .lines()
            .map { it.trim() }

        val start = Point(lines.first().withIndex().first { it.value == '.' }.index, 0)
        val end = Point(lines.last().withIndex().first { it.value == '.' }.index, lines.size - 1)
        val x1 = 0
        val y1 = 0
        val x2 = lines[0].length - 1
        val y2 = lines.size - 1
        val mx = x2 + 1
        val my = x2 + 1

        val obstacles = lines
            .flatMapIndexed { y, s ->
                s.flatMapIndexed { x, c ->
                    when (c) {
                        '#' -> listOf(Wall(p(x, y)))
                        '^' -> listOf(Blizzard(p(x, y), Direction.UP))
                        '>' -> listOf(Blizzard(p(x, y), Direction.RIGHT))
                        'v' -> listOf(Blizzard(p(x, y), Direction.DOWN))
                        '<' -> listOf(Blizzard(p(x, y), Direction.LEFT))
                        else -> listOf()
                    }
                }
            }
            .toSet()


        val turns = LinkedHashSet<Space>()
        turns.add(Space(obstacles))

        while (true) {
            val newObstacles = turns.last().obstacles.map { it.move(x1, x2, y1, y2) }.toSet()
            if (!turns.add(Space(newObstacles))) {
                println("Found loop. Total size: ${turns.size}")
                break
            }
        }

        val maxTurns = turns.size
        val turnMap = turns.map { s ->
            val r = Array(my) { IntArray(mx) }

            s.obstacles.forEach {
                r[it.p.y][it.p.x] = 1
            }

            r
        }

        fun Expedition.explore(): List<Expedition> {
            val nextTurn = turn + 1
            val nX = turnMap[nextTurn % maxTurns]
            val result = mutableListOf<Expedition>()

            if (nX[p.y][p.x] == 0) {
                result.add(this.copy(roundTurn = nextTurn % maxTurns, turn = nextTurn))
            }

            Direction.values().forEach { d ->
                val newX = p.x + d.dp.x
                val newY = p.y + d.dp.y

                if (newX in 0 until mx) {
                    if (newY in 0 until my) {
                        if (nX[newY][newX] == 0) {
                            result.add(Expedition(p(newX, newY), roundTurn = nextTurn % maxTurns, turn = nextTurn))
                        }
                    }
                }
            }

            return result
        }

        fun calculateMinTurns(startTurn: Int, start: Point, end: Point): Int {
            val visited = mutableSetOf<Expedition>()
            val queue = PriorityQueue<Expedition>(Comparator.comparing { it.turn })
            queue.add(Expedition(start, startTurn % maxTurns, startTurn))


            while (queue.isNotEmpty()) {
                val e = queue.poll()
                if (e.p == end) {
                    println("Path start=$startTurn s=$start e=$end takes=${e.turn - startTurn} total=${e.turn}")
                    return e.turn
                }

                e.explore().filter { visited.add(it) }.forEach { queue.add(it) }
            }

            return -1
        }

        val round1Turns = calculateMinTurns(0, start, end)
        val round2Turns = calculateMinTurns(round1Turns, end, start)
        val round3Turns = calculateMinTurns(round2Turns, start, end)
        //921 - too high
    }

}

fun main() {
    Day24.run()
}
