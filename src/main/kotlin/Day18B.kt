import kotlin.math.abs

object Day18B {

    data class Cube(
        val x: Int,
        val y: Int,
        val z: Int,
    ) {
        fun dX(diff: Int): Cube {
            return Cube(x + diff, y, z)
        }

        fun dY(diff: Int): Cube {
            return Cube(x, y + diff, z)
        }

        fun dZ(diff: Int): Cube {
            return Cube(x, y, z + diff)
        }
    }


    fun run() {
        val lines = "day18.txt"
            .readFile()
            .lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }


        val cubes = lines.map { s ->
            val (x, y, z) = s.split(",").map { it.toInt() }
            Cube(x + 1, y + 1, z + 1)
        }

        val mx = cubes.maxOf { it.x } + 5
        val my = cubes.maxOf { it.y } + 5
        val mz = cubes.maxOf { it.z } + 5

        val X = Array(mz) { Array(my) { IntArray(mx) } }

        cubes.forEach {
            X[it.z][it.y][it.x] = 1
        }

        fun countConnections(x: Int, y: Int, z: Int, conn: Int): Int {
            val cube = Cube(x, y, z)
            val list = listOf(
                cube.dX(-1),
                cube.dX(1),
                cube.dY(-1),
                cube.dY(1),
                cube.dZ(-1),
                cube.dZ(1),
            )
                .filter {
                    it.x >= 0 && it.y >= 0 && it.z >= 0
                }
                .filter {
                    it.x < mx && it.y < my && it.z < mz
                }

            val count = list.count { X[it.z][it.y][it.x] == conn }
            return count
        }

        X[0][0][0] = 2

        for (i in 0 until 10) {
            for (z in 0 until mz) {
                for (y in 0 until my) {
                    for (x in 0 until mx) {
                        if (X[z][y][x] != 1) {
                            if (countConnections(x, y, z, 2) > 0) {
                                X[z][y][x] = 2
                            }
                        }
                    }
                }
            }
        }

        var total = 0
        for (z in 0 until mz) {
            for (y in 0 until my) {
                for (x in 0 until mx) {
                    if (X[z][y][x] == 1) {
                        total += countConnections(x, y, z, 2)
                    }
                }
            }
        }

        println(total)
    }

}

fun main() {
    Day18B.run()
}
