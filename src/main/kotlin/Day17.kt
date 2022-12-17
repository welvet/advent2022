import kotlin.math.max

object Day17 {

    enum class Move {
        LEFT, RIGHT
    }

    class Moves(private val s: CharArray) {
        var count = 0

        fun next(): Move {
            val c = s[count++]
            count %= s.size

            return when (c) {
                '<' -> Move.LEFT
                '>' -> Move.RIGHT
                else -> throw IllegalStateException("char: $c")
            }
        }
    }

    data class Point(val x: Int, val y: Long)

    fun p(x: Int, y: Long): Point {
        return Point(x, y)
    }

    fun pp(vararg points: Point): List<Point> {
        return points.toList()
    }

    fun List<Point>.pCopy(dx: Int, dy: Long): List<Point> {
        return this.map {
            Point(it.x + dx, it.y + dy)
        }
    }

    enum class Figures(val create: () -> List<Point>) {
        LINE({ pp(p(0, 0), p(1, 0), p(2, 0), p(3, 0)) }),
        PLUS({ pp(p(1, 0), p(0, 1), p(1, 1), p(2, 1), p(1, 2)) }),
        REVL({ pp(p(0, 0), p(1, 0), p(2, 0), p(2, 1), p(2, 2)) }),
        PILLAR({ pp(p(0, 0), p(0, 1), p(0, 2), p(0, 3)) }),
        SQUARE({ pp(p(0, 0), p(1, 0), p(0, 1), p(1, 1)) })
    }

    private var figureCount = 0
    fun nextFigure(): Figure {
        val f = Figures.values()[figureCount++]
        figureCount %= Figures.values().size

        return Figure(f.create())
    }

    class Figure(
        var points: List<Point>
    ) {
        fun place(floor: List<Long>) {
            val newX = 2
            val newY = floor.max() + 4

            points = points.pCopy(newX, newY)
        }

        fun moveLeft(X: Circle) {
            val newPoints = points.pCopy(-1, 0)
            if (newPoints.all { it.x >= 0 }) {
                if (newPoints.all { X[it.y][it.x] == 0 }) {
                    points = newPoints
                }
            }
        }

        fun moveRight(X: Circle) {
            val newPoints = points.pCopy(1, 0)
            if (newPoints.all { it.x < 7 }) {
                if (newPoints.all { X[it.y][it.x] == 0 }) {
                    points = newPoints
                }
            }
        }

        fun moveDown(X: Circle): Boolean {
            val newPoints = points.pCopy(0, -1)
            if (newPoints.all { it.y >= 0 }) {
                if (newPoints.all { X[it.y][it.x] == 0 }) {
                    points = newPoints
                    return true
                }
            }

            return false
        }
    }

    class Circle(val size: Int = 500) {
        private val X = Array(size) { IntArray(7) }
        private var currentHeight: Long = 0
        private var rowOffset: Long = 0

        operator fun get(index: Long): IntArray {
            if (index >= currentHeight) {
                addHeight(index - currentHeight + 1)
            }

            check(index < currentHeight) { "ch=$currentHeight x=$index" }
            return X[pos(index)]
        }

        private fun addHeight(h: Long) {
            for (i in currentHeight until (currentHeight + h)) {
                X[pos(i)] = IntArray(7)
            }

            currentHeight += h
        }

        private fun pos(x: Long): Int {
            return ((x - rowOffset) % size).toInt()
        }

        fun mixHeight(heightToAdd: Long) {
            currentHeight += heightToAdd
            rowOffset += heightToAdd
            rowOffset %= size
        }
    }


    class Area {
        var figures: Long = 0
        private val floor: MutableList<Long> = mutableListOf(-1, -1, -1, -1, -1, -1, -1)
        private val X: Circle = Circle()

        fun addFigure(f: Figure) {
            f.points.forEach { p ->
                floor[p.x] = max(floor[p.x], p.y)
            }

            f.points.forEach {
                if (X[it.y][it.x] == 1) {
                    throw IllegalStateException("Overlaps! x=${it.x} y=${it.y}")
                }
                X[it.y][it.x] = 1
            }

            figures++
        }

        fun getFloor(): List<Long> {
            return floor
        }

        fun getX(): Circle {
            return X
        }

        fun mix(figuresToAdd: Long, heightToAdd: Long) {
            figures += figuresToAdd
            X.mixHeight(heightToAdd)
            for (i in 0 until floor.size) {
                floor[i] += heightToAdd
            }
        }
    }


    fun run() {
        val lines = "day17.txt"
            .readFile()
            .lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val moves = Moves(lines[0].toCharArray())
        val area = Area()

        var figure: Figure? = null
        val total = 1000000000000L

        var cycleStarts = false

        var lastF = 0L
        var lastH = 0L
        var lastDiffF = 0L
        var lastDiffH = 0L

        while (true) {
            if (moves.count == 0) {
                cycleStarts = true
            }

            if (figure == null) {
                if (cycleStarts && figureCount == 0) {
                    cycleStarts = false
                    val f = area.figures
                    val h = area.getFloor().max() + 1
                    val diffF = f - lastF
                    val diffH = h - lastH

                    println("Current circle Figures=$f height=$h Diff f=$diffF h=$diffH")

                    if (h > 0 && diffF == lastDiffF && diffH == lastDiffH) {
                        val multiplier = (total - area.figures) / diffF
                        println("Circle found! Multiplying by $multiplier")
                        area.mix(multiplier * diffF, multiplier * diffH)
                        println(
                            "Added f=${multiplier * diffF} h=${multiplier * diffH}. New area f=${area.figures} h=${
                                area.getFloor().max() + 1
                            }"
                        )
                    }

                    lastF = f
                    lastH = h
                    lastDiffF = diffF
                    lastDiffH = diffH
                }

                figure = nextFigure()
                figure.place(area.getFloor())
            }

            when (moves.next()) {
                Move.LEFT -> figure.moveLeft(area.getX())
                Move.RIGHT -> figure.moveRight(area.getX())
            }

            if (!figure.moveDown(area.getX())) {
                area.addFigure(figure)

                if (area.figures == total) {
                    break
                }

                figure = null
            }
        }

        println(area.getFloor().max() + 1)
    }

}

fun main() {
    Day17.run()
}
