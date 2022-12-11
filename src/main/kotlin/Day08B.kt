import java.util.TreeMap

fun main(args: Array<String>) {
    val lines = "day08.txt"
        .readFile()
        .lines()
        .map { it.trim() }
        .filter { it.isNotBlank() }

    val iMax = lines.size
    val jMax = lines[0].length

    val X = Array(iMax) { IntArray(jMax) }
    val Y = Array(4) { Array(iMax) { IntArray(jMax) } }
    val Z = Array(iMax) { IntArray(jMax) }

    lines.forEachIndexed { i, s ->
        s.toCharArray().forEachIndexed { j, c ->
            X[i][j] = c.toString().toInt()
        }
    }

    class Finder {
        private val distance: TreeMap<Int, Int> = TreeMap<Int, Int>()
        private var pos = -1

        fun tree(height: Int):Int {
            pos++

            val found = distance.ceilingEntry(height)
            distance[height] = pos

            return if (found != null) {
                pos - found.value
            } else {
                pos
            }
        }
    }



    for (i in 0 until iMax) {
        var finder = Finder()
        for (j in 0 until jMax) {
            Y[0][i][j] += finder.tree(X[i][j])
        }

        finder = Finder()
        for (j in jMax - 1 downTo 0) {
            Y[1][i][j] += finder.tree(X[i][j])
        }
    }

    for (j in 0 until jMax) {
        var finder = Finder()
        for (i in 0 until iMax) {
            Y[2][i][j] += finder.tree(X[i][j])
        }

        finder = Finder()
        for (i in iMax - 1 downTo 0) {
            Y[3][i][j] += finder.tree(X[i][j])
        }
    }

    var max = 0
    for (i in 0 until iMax) {
        for (j in 0 until jMax) {
            Z[i][j] = Y[0][i][j] * Y[1][i][j] * Y[2][i][j] * Y[3][i][j]
            max = Z[i][j].coerceAtLeast(max)
        }
    }


    println(max)

}



