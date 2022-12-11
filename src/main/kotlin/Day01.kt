fun main(args: Array<String>) {
    val lines = "day01.txt".readFile().lines()

    var max = 0
    var current = 0
    lines.forEach {
        if (it.isBlank()) {
            if (current > max) {
                max = current
            }

            current = 0
        } else {
            current += it.trim().toInt()
        }
    }

    println("Result: $max")
}
