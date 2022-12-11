import java.util.LinkedList
import java.util.Stack

private fun findPos(s: String): Int {
    val unique = 13
    val chars = s.toCharArray()
    val list = LinkedList<Char>()

    chars.take(unique).forEach(list::add)

    for (i in unique until chars.size) {
        val c = chars[i]
        list.add(c)

        if (list.distinct().count() == unique + 1) {
            return i + 1
        }

        list.removeFirst()
    }

    return -1
}

fun main(args: Array<String>) {
    val lines = "day06_test.txt".readFile().lines()

    lines
        .filter { it.isNotBlank() }
        .map {
            findPos(it)
        }
        .forEach {
            println("Result: $it")
        }

}


