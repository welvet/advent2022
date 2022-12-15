import kotlin.math.abs

object Day15 {

    data class Sensor(
        val sx: Long,
        val sy: Long,
        val bx: Long,
        val by: Long
    ) {
        private fun maxDistance(): Long {
            return abs(sx - bx) + abs(sy - by)
        }

        fun distanceIn(y: Long): Long {
            return maxDistance() - abs(sy - y)
        }

        fun startIn(y: Long): Long {
            return sx - distanceIn(y)
        }

        fun endIn(y: Long) : Long {
            return sx + distanceIn(y)
        }
    }

    fun run() {
        val lines = "day15.txt"
            .readFile()
            .lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val reg = "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)".toRegex()

        val sensors = lines
            .map { reg.find(it)!! }
            .map { mr ->
                val (sx, sy, bx, by) = mr.destructured
                Sensor(sx.toLong(), sy.toLong(), bx.toLong(), by.toLong())
            }

        val y = 2000000L
        val result = mutableSetOf<Long>()

        sensors
            .filter { s -> s.distanceIn(y) > 0 }
            .forEach { s ->
                val r = s.startIn(y)..s.endIn(y)
                println("For x=${s.sx} y=${s.sy} bx=${s.bx} by=${s.by} dist=${s.distanceIn(y)} res=${r}")
                result.addAll(r)
            }

        sensors
            .filter { it.by == y }
            .forEach { result.remove(it.bx) }

        println("result ${result.size}")
    }


}

fun main() {
    Day15.run()
}
