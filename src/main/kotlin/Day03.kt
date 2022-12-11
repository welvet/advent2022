

fun main(args: Array<String>) {
    val lines = "day03.txt".readFile().lines()

    var sum = 0
    lines.forEach { line ->
        if (line.isBlank()) {
            return@forEach
        }
        val chars = line.toCharArray().toList()
        val left = chars.subList(0, chars.size / 2)
        val right = chars.subList(chars.size / 2, chars.size)

        val intersect = left.intersect(right)

        sum += intersect.map {
            if (it.isLowerCase()) {
                it - 'a' + 1
            } else {
                it - 'A' + 27
            }
        }.sum()
    }

    println("Result: $sum")
}

