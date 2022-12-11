
fun main(args: Array<String>) {
    val lines = "day08.txt"
        .readFile()
        .lines()
        .map { it.trim() }
        .filter { it.isNotBlank() }

    val iMax = lines.size
    val jMax = lines[0].length

    val X = Array(iMax) { IntArray(jMax) }

    lines.forEachIndexed { i, s ->
        s.toCharArray().forEachIndexed { j, c ->
            X[i][j] = c.toString().toInt()
        }
    }

    var total = 0
    val counted = mutableSetOf<Pair<Int, Int>>()
    fun tryCount(i : Int, j : Int) {
        if (counted.add(Pair(i, j))) {
            if (i != 0 && j != 0 && i != 4 && j != 4) {
                println("adding $i $j")
            }
            total++
        }
    }

    for (i in 0 until iMax) {
        var currentMax = -1
        for (j in 0 until jMax) {
            if (X[i][j] > currentMax) {
                currentMax = X[i][j]
                tryCount(i, j)
            }
        }

        currentMax = -1
        for (j in jMax - 1 downTo 0) {
            if (X[i][j] > currentMax) {
                currentMax = X[i][j]
                tryCount(i, j)
            }
        }
    }

    for (j in 0 until jMax) {
        var currentMax = -1
        for (i in 0 until iMax) {
            if (X[i][j] > currentMax) {
                currentMax = X[i][j]
                tryCount(i, j)
            }
        }

        currentMax = -1
        for (i in iMax - 1 downTo 0) {
            if (X[i][j] > currentMax) {
                currentMax = X[i][j]
                tryCount(i, j)
            }
        }
    }


    println(total)
}



