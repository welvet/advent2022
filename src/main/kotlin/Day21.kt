import java.lang.IllegalStateException

object Day21 {

    interface Operation {
        fun value(): Long
    }

    fun run() {
        val lines = "day21.txt"
            .readFile()
            .lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val monkeys = mutableMapOf<String, Operation>()

        class Fixed(val v: Long) : Operation {
            override fun value(): Long {
                return v
            }
        }

        class Calculation(
            val left: String,
            var operation: String,
            val right: String
        ) : Operation {
            var result: Long? = null

            override fun value(): Long {
                result?.let { return it }

                val leftVal = monkeys[left]!!.value()
                val rightVal = monkeys[right]!!.value()

                val res = when (operation) {
                    "+" -> leftVal + rightVal
                    "-" -> leftVal - rightVal
                    "/" -> leftVal / rightVal
                    "*" -> leftVal * rightVal
                    else -> {
                        throw IllegalStateException(operation)
                    }
                }

                result = res
                return res
            }
        }

        val p = "([a-z]+) ([*/\\-+]?) ([a-z]+)".toRegex()
        lines
            .map { s ->
                val (k, data) = s.split(":").map { it.trim() }
                val matchResult = p.find(data)
                k to if (matchResult == null) {
                    Fixed(data.toLong())
                } else {
                    val (l, o, r) = matchResult.destructured
                    Calculation(l, o, r)
                }
            }
            .forEach {
                monkeys[it.first] = it.second
            }

        val result = monkeys["root"]!!.value()
        println("Res = $result")
    }

}

fun main() {
    Day21.run()
}
