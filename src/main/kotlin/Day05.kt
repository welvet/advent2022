import java.util.Stack


private fun parse(s: String): IntRange {
    val array = s.split("-")
    val start = array[0].toInt()
    val end = array[1].toInt()
    return start..end
}

fun main(args: Array<String>) {
    val pos = (0..10).associateBy {
        1 + it + 3 * it
    }

    val breakLine = 9 // starts 0
    val lines = "day05.txt".readFile().lines()

    val inputStacks = lines.subList(0, breakLine)
    val dataB = lines.subList(breakLine + 1, lines.size)

    val stacks = mutableMapOf<Int, Stack<Char>>()
    inputStacks
        .take(inputStacks.size - 1)
        .reversed()
        .forEach { line ->
            line.toCharArray().forEachIndexed { i, char ->
                if (char.isUpperCase()) {
                    val target = stacks.getOrPut(pos[i]!!) { Stack<Char>() }
                    target.push(char)

                }
            }
        }

    val regex = "move ([0-9]+) from ([0-9]+) to ([0-9]+)".toRegex()
    dataB
        .filter { it.isNotBlank() }
        .map {
            val (total, from, to) = regex.find(it)!!.destructured
            val fromStack = stacks[from.toInt() - 1]!!
            val toStack = stacks[to.toInt() - 1]!!

            repeat(total.toInt()) {
                val v = fromStack.pop()
                toStack.push(v)
            }
        }

    val result = stacks.entries
        .asSequence()
        .sortedBy { it.key }
        .map { it.value }
        .filter { it.isNotEmpty() }
        .map { it.peek() }
        .joinToString(separator = "") { it.toString() }

    println("Result: $result")
}


