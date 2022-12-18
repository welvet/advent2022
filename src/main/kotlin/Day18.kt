import kotlin.math.abs

object Day18 {

    data class Cube(
        val x: Int,
        val y: Int,
        val z: Int,
        val connections: MutableList<Cube> = mutableListOf()
    ) {
        fun hasConnections(c: Cube):Boolean {
            return abs(x - c.x) + abs(y - c.y) + abs(z - c.z) == 1
        }

        fun addCube(c: Cube) {
            if (hasConnections(c)) {
                connections.add(c)
            }
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
            Cube(x, y, z)
        }

        for (current in cubes) {
            for (other in cubes) {
                if (current == other) {
                    continue
                }

                current.addCube(other)
            }
        }

        val result = cubes.sumOf { 6 - it.connections.size }

        println(result)
    }

}

fun main() {
    Day18.run()
}
