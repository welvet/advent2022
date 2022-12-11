private fun parse(s: String): IntRange {
    val array = s.split("-")
    val start = array[0].toInt()
    val end = array[1].toInt()
    return start..end
}

fun main(args: Array<String>) {
    val lines = "day04_test.txt".readFile().lines()

    val result =
        lines
            .filter { it.isNotBlank() }
            .map { line ->
                val array = line.split(",")
                Pair(array[0], array[1])
            }
            .map { (a, b) ->
                Pair(parse(a), parse(b))
            }
            .filter { (a, b) ->
                val intersect = a.intersect(b)
                intersect.isNotEmpty()
            }
            .count()

    println("Result: $result")
}


