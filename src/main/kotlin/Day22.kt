import java.lang.IllegalArgumentException

object Day22 {

    enum class Tile {
        EMPTY, OPEN, WALL;
    }

    enum class Direction(val id: Int, val dx: Int, val dy: Int, val s: String) {
        RIGHT(0, 1, 0, ">"), DOWN(1, 0, 1, "v"), LEFT(2, -1, 0, "<"), UP(3, 0, -1, "^");

        fun right(): Direction {
            return when (this) {
                RIGHT -> DOWN
                DOWN -> LEFT
                LEFT -> UP
                UP -> RIGHT
            }
        }

        fun left(): Direction {
            return when (this) {
                RIGHT -> UP
                UP -> LEFT
                LEFT -> DOWN
                DOWN -> RIGHT
            }
        }
    }

    fun run() {
        val lines = "day22_test.txt"
            .readFile()
            .lines()

        val (map, route) = lines.split(false) { it.isBlank() }

        val maxX = map.maxOf { it.length }
        val maxY = map.size
        val X = Array(maxY) { Array(maxX) { Tile.EMPTY } }

        map.forEachIndexed { y, s ->
            s.toCharArray().forEachIndexed { x, c ->
                when (c) {
                    ' ' -> X[y][x] = Tile.EMPTY
                    '.' -> X[y][x] = Tile.OPEN
                    '#' -> X[y][x] = Tile.WALL
                    else -> throw IllegalArgumentException("Char: $c")
                }
            }
        }

        class Person(
            var x: Int,
            var y: Int,
            var direction: Direction
        ) {
            fun result(): Int {
                return 1000 * (y + 1) + 4 * (x + 1) + direction.id
            }
        }

        class Move(var distance: Int) : (Person) -> Unit {
            override fun invoke(p: Person) {
                var newX = p.x
                var newY = p.y

                while(distance > 0) {
                    newX += p.direction.dx
                    newY += p.direction.dy

                    newX %= maxX
                    newY %= maxY

                    if (newX == -1) {
                        newX = maxX - 1
                    }
                    if (newY == -1) {
                        newY = maxY - 1
                    }

//                    println("Trying $newX $newY: ${X[newY][newX]}")
                    if (X[newY][newX] == Tile.OPEN) {
                        p.x = newX
                        p.y = newY
                        distance--
                    } else if (X[newY][newX] == Tile.WALL) {
                        return
                    }
                }
            }

            override fun toString(): String {
                return "M$distance"
            }

        }

        class Rotate(val direction: Direction) : (Person) -> Unit {
            override fun invoke(p: Person) {
                when (direction) {
                    Direction.RIGHT -> p.direction = p.direction.right()
                    Direction.LEFT -> p.direction = p.direction.left()
                    else -> {
                        throw IllegalArgumentException(direction.toString())
                    }
                }
            }

            override fun toString(): String {
                return "R$direction"
            }

        }

        val moves = mutableListOf<(Person) -> Unit>()
        val current = StringBuilder()
        route[0].forEach {
            if (it.isDigit()) {
                current.append(it)
            } else {
                if (current.isNotEmpty()) {
                    moves.add(Move(current.toString().toInt()))
                    current.clear()
                }

                when (it) {
                    'R' -> moves.add(Rotate(Direction.RIGHT))
                    'L' -> moves.add(Rotate(Direction.LEFT))
                }
            }
        }
        if (current.isNotEmpty()) {
            moves.add(Move(current.toString().toInt()))
        }


        val person = Person(0, 0, Direction.RIGHT)

        outer@ for (y in 0 until maxY) {
            for (x in 0 until maxX) {
                if (X[y][x] == Tile.OPEN) {
                    person.x = x
                    person.y = y
                    break@outer
                }
            }
        }

        fun printX() {
            for (y in 0 until maxY) {
                for (x in 0 until maxX) {
                    if (person.x == x && person.y == y) {
                        print(person.direction.s)
                    } else {
                        when (X[y][x]) {
                            Tile.EMPTY -> print(" ")
                            Tile.OPEN -> print(".")
                            Tile.WALL -> print("#")
                        }
                    }
                }
                println()
            }

            println()
            println()
        }

//        printX()
        moves.forEach {
//            println("Turn: $it")
            it(person)
//            printX()
        }

        println(person.result())
    }

}

fun main() {
    Day22.run()
}
