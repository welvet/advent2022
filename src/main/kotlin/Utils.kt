import java.io.File

fun String.readFile(): String {
    return File("/Users/alekseik/software/adventofcode2022/data/$this").readText()
}

fun <T> List<T>.split(predicate: (T) -> Boolean): List<List<T>> {
    val result = mutableListOf<List<T>>()
    var current = mutableListOf<T>()

    for (el in this) {
        if (predicate(el)) {
            if (current.isNotEmpty()) {
                result.add(current)
                current = mutableListOf()
            }
        }
        current.add(el)
    }

    if (current.isNotEmpty()) {
        result.add(current)
    }

    return result
}
