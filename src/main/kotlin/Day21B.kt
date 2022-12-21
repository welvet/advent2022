import java.lang.IllegalStateException

object Day21B {

    interface Operation {
        fun value(): String
    }

    fun run() {
        val lines = "day21.txt"
            .readFile()
            .lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val monkeys = mutableMapOf<String, Operation>()

        class Fixed(val v: Long) : Operation {
            override fun value(): String {
                return v.toString()
            }
        }

        class Human() : Operation {
            override fun value(): String {
                return "X"
            }
        }

        class Root(val left: String, var right: String) : Operation {
            override fun value(): String {
                val leftVal = monkeys[left]!!.value()
                val rightVal = monkeys[right]!!.value()

                return "$leftVal = $rightVal"
            }
        }

        class Calculation(
            val left: String,
            var operation: String,
            val right: String
        ) : Operation {
            override fun value(): String {
                val leftVal = monkeys[left]!!.value()
                val rightVal = monkeys[right]!!.value()

                if (!leftVal.contains("X") && !rightVal.contains("X")) {
                    val l = leftVal.toLong()
                    val r = rightVal.toLong()

                    return when (operation) {
                        "+" -> l + r
                        "-" -> l - r
                        "/" -> l / r
                        "*" -> l * r
                        else -> {
                            throw IllegalStateException(operation)
                        }
                    }.toString()
                }

                return when (operation) {
                    "+" -> "($leftVal + $rightVal)"
                    "-" -> "($leftVal - $rightVal)"
                    "/" -> "($leftVal / $rightVal)"
                    "*" -> "($leftVal * $rightVal)"
                    else -> {
                        throw IllegalStateException(operation)
                    }
                }
            }
        }

        val p = "([a-z]+) ([*/\\-+]?) ([a-z]+)".toRegex()
        lines
            .map { s ->
                val (k, data) = s.split(":").map { it.trim() }
                val matchResult = p.find(data)

                k to if (k == "humn") {
                    Human()
                } else {
                    if (matchResult == null) {
                        Fixed(data.toLong())
                    } else {
                        val (l, o, r) = matchResult.destructured
                        if (k == "root") {
                            Root(l, r)
                        } else {
                            Calculation(l, o, r)
                        }
                    }
                }
            }
            .forEach {
                monkeys[it.first] = it.second
            }

        val result = monkeys["root"]!!.value()
        //https://www.mathpapa.com/equation-solver/
        println("$result")
    }

}

fun main() {
    Day21B.run()
}
