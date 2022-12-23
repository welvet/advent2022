import java.util.LinkedList

object Day23 {

    data class Point(val x: Int, val y: Int)

    fun p(x: Int, y: Int): Point {
        return Point(x, y)
    }

    data class Move(
        val direction: Direction,
        val points: List<Point>,
        val destination: Point
    )

    enum class Direction(val lookupDiff: List<Point>, val moveDiff: Point) {
        NORTH(listOf(p(-1, -1), p(0, -1), p(1, -1)), p(0, -1)),
        SOUTH(listOf(p(-1, 1), p(0, 1), p(1, 1)), p(0, 1)),

        WEST(listOf(p(-1, -1), p(-1, 0), p(-1, 1)), p(-1, 0)),
        EAST(listOf(p(1, -1), p(1, 0), p(1, 1)), p(1, 0))
    }

    val lookPositionDiff = Direction.values().flatMap { it.lookupDiff }.toSet()

    data class Elf(
        val id: Int,
        var x: Int,
        var y: Int
    ) {
        private var currentMoves: List<Move> = listOf()
        val directions = LinkedList(Direction.values().toList())

        fun createMoves(elvesMap: Set<Point>): List<Move> {
            currentMoves = listOf()

            val hasElvesAround = lookPositionDiff
                .map { diff ->
                    Point(x + diff.x, y + diff.y)
                }
                .any { elvesMap.contains(it) }

            if (hasElvesAround) {
                directions
                    .forEachIndexed { did, dir ->
                        val watchList = dir
                            .lookupDiff
                            .map { diff ->
                                Point(x + diff.x, y + diff.y)
                            }
                            .filter { !elvesMap.contains(it) }

                        if (watchList.size == dir.lookupDiff.size) {
//                            println("Elf $id wants to move ${dir.name}")
                            val destination = Point(
                                x + dir.moveDiff.x,
                                y + dir.moveDiff.y
                            )

                            currentMoves = listOf(Move(dir, watchList, destination))
                            return currentMoves
                        }
                    }
            }

            return currentMoves
        }

        fun tryMove(moveMap: Map<Point, Set<Int>>): Boolean {
            directions.add(directions.removeFirst())

            currentMoves.firstOrNull { move ->
                moveMap[move.destination]!!.size == 1
            }?.let { move ->
//                println("Moving $id to ${move.destination}")
                x = move.destination.x
                y = move.destination.y
                return true
            }

            return false
        }

    }

    fun run() {
        val lines = "day23.txt"
            .readFile()
            .lines()
            .map { it.trim() }

        var id = 0
        val elves = lines.flatMapIndexed { y, s ->
            s.toCharArray().flatMapIndexed { x, c ->
                if (c == '#') {
                    listOf(Elf(id++, x, y))
                } else {
                    listOf()
                }
            }
        }


        fun printX() {
            val elvesMap = elves.map {
                Point(it.x, it.y)
            }.toSet()

            val x1 = elves.minOf { it.x }
            val x2 = elves.maxOf { it.x }

            val y1 = elves.minOf { it.y }
            val y2 = elves.maxOf { it.y }
            for (y in y1..y2) {
                for (x in x1..x2) {
                    if (elvesMap.contains(Point(x, y))) {
                        print('#')
                    } else {
                        print(".")
                    }
                }
                println()
            }

            println()
            println()
        }

//        printX()
        var someoneMoved = true
        var round = 0
        while (someoneMoved) {
            val elvesMap = elves.map {
                Point(it.x, it.y)
            }.toSet()

            val space = mutableMapOf<Point, MutableSet<Int>>()
            elves.forEach { e ->
                e.createMoves(elvesMap).forEach { move ->
                    space.getOrPut(move.destination) { mutableSetOf() }.add(e.id)
                }
            }

            someoneMoved = false
            elves.forEach { e ->
                someoneMoved = e.tryMove(space) || someoneMoved
            }
            round++
//            printX()
        }
        println("Round: $round")

        val x1 = elves.minOf { it.x }
        val x2 = elves.maxOf { it.x } + 1

        val y1 = elves.minOf { it.y }
        val y2 = elves.maxOf { it.y } + 1

        val result = (x2 - x1) * (y2 - y1) - elves.size
        println(result)
    }

}

fun main() {
    Day23.run()
}
