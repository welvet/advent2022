object Day22B {

    enum class TileType {
        EMPTY, OPEN, WALL;
    }

    data class Tile(
        val type: TileType,
        var x: Int = 0,
        var y: Int = 0,
        var up: Tile? = null,
        var right: Tile? = null,
        var down: Tile? = null,
        var left: Tile? = null,
        var upRotate: Int = 0,
        var rightRotate: Int = 0,
        var downRotate: Int = 0,
        var leftRotate: Int = 0,
    ) {
        override fun toString(): String {
            return when (type) {
                TileType.EMPTY -> " "
                TileType.OPEN -> "."
                TileType.WALL -> "#"
            }
        }
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

    data class SideAndRotate(
        val sideId: Int,
        val rotate: Int
    )

    data class Side(
        val sideId: Int,
        val top: SideAndRotate,
        val right: SideAndRotate,
        val bottom: SideAndRotate,
        val left: SideAndRotate
    )

    fun run() {
        val mode = "prod"

        val lines: List<String>
        val width: Int
        val connections: List<String>
        val gridX : Int
        val gridY : Int

        if (mode == "test") {
            lines = "day22_test.txt".readFile().lines()
            width = 4
            gridX = 4
            gridY = 4
            connections = listOf(
                "2:4|2 11|2 6|0 5|1",
                "4:2|2 5|0 10|2 11|3",
                "5:2|3 6|0 10|1 4|0",
                "6:2|0 11|3 10|0 5|0",
                "10:6|0 11|0 4|2 5|3",
                "11:6|1 2|2 4|1 10|0",
            )
        } else {
            lines = "day22.txt".readFile().lines()
            width = 50
            gridX = 3
            gridY = 4
            connections = listOf(
                "1:9|3 2|0 4|0 6|2",
                "2:9|0 7|2 4|3 1|0",
                "4:1|0 2|1 7|0 6|1",
                "6:4|3 7|0 9|0 1|2",
                "7:4|0 2|2 9|3 6|0",
                "9:6|0 7|1 2|0 1|1",
            )
        }

        val sides = connections.associate { s ->
            val (id, conn) = s.split(":")
            val cc = conn.split(" ").map {
                val (side, rotate) = it.split("|").map { it.toInt() }
                SideAndRotate(side, rotate)
            }

            id.toInt() to Side(
                id.toInt(),
                cc[0],
                cc[1],
                cc[2],
                cc[3]
            )
        }

        val (map, route) = lines.split(false) { it.isBlank() }

        val maxX = width * gridX
        val maxY = width * gridY
        val X = Array(maxY) { Array(maxX) { Tile(TileType.EMPTY) } }


        fun Array<Array<Tile>>.rotate(): Array<Array<Tile>> {
            val result = Array(width) {
                Array(width) {
                    Tile(TileType.EMPTY)
                }
            }

            for (y in 0 until width) {
                for (x in 0 until width) {
                    result[x][width - 1 - y] = this[y][x]
                }
            }

            return result
        }

        fun side(id: Int): Array<Array<Tile>> {
            val result = Array(width) {
                Array(width) {
                    Tile(TileType.EMPTY)
                }
            }

            val row = id / gridX
            val col = id % gridX

            for (y in 0 until width) {
                for (x in 0 until width) {
                    val otherX = x + col * width
                    val otherY = y + row * width

                    result[y][x] = X[otherY][otherX]
                }
            }

            return result
        }

        fun SideAndRotate.get(target: Int): Array<Array<Tile>> {
            println("Side ${this.sideId} rotating ${this.rotate} for $target")
            var s = side(this.sideId)
            repeat(this.rotate) {
                s = s.rotate()
            }

            return s
        }

        map.forEachIndexed { y, s ->
            s.toCharArray().forEachIndexed { x, c ->
                when (c) {
                    ' ' -> X[y][x] = Tile(TileType.EMPTY)
                    '.' -> X[y][x] = Tile(TileType.OPEN)
                    '#' -> X[y][x] = Tile(TileType.WALL)
                    else -> throw IllegalArgumentException("Char: $c")
                }

                X[y][x].x = x
                X[y][x].y = y
            }
        }

        sides.values.forEach { s ->
            val thisSide = side(s.sideId)

            //top
            val topSide = s.top.get(s.sideId)
            for (x in 0 until width) {
                thisSide[0][x].up = topSide[width - 1][x]
                thisSide[0][x].upRotate = s.top.rotate
            }

            //right
            val rightSide = s.right.get(s.sideId)
            for (y in 0 until width) {
                thisSide[y][width - 1].right = rightSide[y][0]
                thisSide[y][width - 1].rightRotate = s.right.rotate
            }

            //bottom
            val bottomSide = s.bottom.get(s.sideId)
            for (x in 0 until width) {
                thisSide[width - 1][x].down = bottomSide[0][x]
                thisSide[width - 1][x].downRotate = s.bottom.rotate
            }

            //left
            val leftSide = s.left.get(s.sideId)
            for (y in 0 until width) {
                thisSide[y][0].left = leftSide[y][width - 1]
                thisSide[y][0].leftRotate = s.left.rotate
            }
        }

        data class Person(
            var x: Int,
            var y: Int,
            var direction: Direction
        )

        class Move(var distance: Int) : (Person) -> Unit {
            override fun invoke(p: Person) {
                while (distance > 0) {
                    val currentTile = X[p.y][p.x]

                    val nextTileIsAJump = when (p.direction) {
                        Direction.RIGHT -> currentTile.right
                        Direction.DOWN -> currentTile.down
                        Direction.LEFT -> currentTile.left
                        Direction.UP -> currentTile.up
                    }

                    if (nextTileIsAJump != null) {
                        if (nextTileIsAJump.type == TileType.WALL) {
                            return
                        }

                        val turns = when (p.direction) {
                            Direction.RIGHT -> currentTile.rightRotate
                            Direction.DOWN -> currentTile.downRotate
                            Direction.LEFT -> currentTile.leftRotate
                            Direction.UP -> currentTile.upRotate
                        }

                        repeat(turns) {
                            p.direction = p.direction.left()
                        }

                        p.x = nextTileIsAJump.x
                        p.y = nextTileIsAJump.y
                    } else {
                        val newX = p.x + p.direction.dx
                        val newY = p.y + p.direction.dy

                        if (X[newY][newX].type == TileType.OPEN) {
                            p.x = newX
                            p.y = newY
                        } else if (X[newY][newX].type == TileType.WALL) {
                            return
                        }
                    }

                    distance--
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
                if (X[y][x].type == TileType.OPEN) {
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
                        when (X[y][x].type) {
                            TileType.EMPTY -> print(" ")
                            TileType.OPEN -> print(".")
                            TileType.WALL -> print("#")
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
            println("Turn: $it")
            it(person)
//            printX()
        }


        val x = person.x
        val y = person.y
        val result = 1000 * (y + 1) + 4 * (x + 1) + person.direction.id

        //133174
        println("x=${x + 1} y=${y + 1} d=${person.direction} r=$result")

        for (i in 0..3) {
            val result2 = 1000 * (y + 1) + 4 * (x + 1) + i
            println("Result could be: $result2")
        }

    }

}

fun main() {
    Day22B.run()
}
