fun main(args: Array<String>) {
    val lines = "day03.txt".readFile().lines()

    val sum = lines
        .filter { it.isNotBlank() }
        .map { it.toCharArray().toList() }
        .chunked(3)
        .map { group ->
            group
                .map { it.toSet() }
                .flatten()
                .groupBy { it }
                .maxBy { it.value.size }
                .key
        }.map {
            if (it.isLowerCase()) {
                it - 'a' + 1
            } else {
                it - 'A' + 27
            }
        }
        .sum()

    println("Result: $sum")
}

