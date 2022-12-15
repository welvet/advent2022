import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object Day15B {

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

        fun endIn(y: Long): Long {
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

        val max = 4000000L

        (0..max)
            .toList()
            .parallelStream()
            .forEach { y ->
                val ranges = sensors
                    .filter { s -> s.distanceIn(y) > 0 }
                    .map { s ->
                        max(0, s.startIn(y))..min(max, s.endIn(y))
                    }
                    .sortedBy { it.first }

                val rangesList = mutableListOf<LongRange>()
                ranges.forEach { currentRange ->
                    var first = currentRange.first
                    var last = currentRange.last

                    rangesList.forEach { otherRange ->
                        first = max(first, otherRange.last)
                        if (otherRange.last == first) {
                            first++
                        }

                        last = max(last, otherRange.last)
                    }

                    if (first <= last) {
                        rangesList.add(first..last)
                    }
                }

                val total = rangesList.sumOf { it.last + 1 - it.first }
//                println("y=$y size=${total} $rangesList")

                if (total == max) {
                    //won't work with x=0
                    for (i in 1 until rangesList.size) {
                        val prev = rangesList[i - 1]
                        val curr = rangesList[i]

                        if (prev.last + 1 < curr.first) {
                            val x = curr.first - 1
                            println("Found! Result: x=${x} y=${y} t=${4000000 * x + y}")
                        }
                    }
                }
            }
    }


}

fun main() {
    Day15B.run()
}
