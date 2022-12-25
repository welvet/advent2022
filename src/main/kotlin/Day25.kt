import java.lang.IllegalArgumentException
import java.math.BigInteger
import java.util.LinkedHashSet
import java.util.PriorityQueue
import kotlin.math.pow

object Day25 {

    fun run() {
        val lines = "day25.txt"
            .readFile()
            .lines()
            .map { it.trim() }

        listOf("1", "2", "1=", "1-", "10", "1121-1110-1=0").forEach {
            println("$it cov ${convert(it)}")
        }

        val res = lines.sumOf { l -> convert(l) }

        println("Dec: $res")

        //1349752690 wrong
        //35593435781062 wrong
        val sb = StringBuilder("2")
        while (true) {
            val current = convert(sb.toString())
            if (current.compareTo(res) == 1) {
                break
            }
            sb.append("2")
        }

        println("Current max $sb")

        var chars = sb.toString().toCharArray()
        var pos = 0
        while (true) {
            if (convert(String(chars)) == res) {
                break
            }

            val charCopy = chars.copyOf()
            charCopy[pos]= minusChar(charCopy[pos])

            val current = convert(String(charCopy))
            if (current.compareTo(res) == -1) {
                pos++
            } else {
                chars = charCopy
                if (charCopy[pos] == '=') {
                    pos++
                }
            }
        }

        println("String ${String(chars)}")
    }

    private fun minusChar(c: Char): Char {
        return when (c) {
            '2' -> '1'
            '1' -> '0'
            '0' -> '-'
            '-' -> '='
            else -> throw IllegalStateException("Unable to covert $c")
        }
    }

    private fun convert(l: String): BigInteger = l
        .toCharArray()
        .toList()
        .reversed()
        .mapIndexed { i, c ->
            val base = BigInteger.valueOf(5).pow(i)

            base.multiply(
                when (c) {
                    '2' -> 2
                    '1' -> 1
                    '0' -> 0
                    '-' -> -1
                    '=' -> -2
                    else -> throw IllegalArgumentException("Got: $c")
                }.toBigInteger()
            )
        }
        .reduce { acc, a ->
            acc.plus(a)
        }

}

fun main() {
    Day25.run()
}
