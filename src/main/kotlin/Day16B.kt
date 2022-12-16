import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.jvm.optionals.getOrNull

object Day16B {

    data class Valve(
        val name: String,
        val rate: Int,
        val connections: Set<String>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Valve

            if (name != other.name) return false

            return true
        }

        override fun hashCode(): Int {
            return name.hashCode()
        }
    }

    data class OpenValve(
        val valve: Valve,
        val activeTurns: Int,
        val total: Int,
        val next: OpenValve?
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as OpenValve

            if (valve != other.valve) return false
            if (activeTurns != other.activeTurns) return false
            if (total != other.total) return false

            return true
        }

        override fun hashCode(): Int {
            var result = valve.hashCode()
            result = 31 * result + activeTurns
            result = 31 * result + total
            return result
        }
    }


    private val cache: MutableMap<String, Optional<OpenValve>> = ConcurrentHashMap(Int.MAX_VALUE / 4)

    data class Variations(
        val a: Set<String>,
        val b: Set<String>
    ) {
        fun addToA(s: String): Variations {
            return Variations(a + s, b)
        }

        fun addToB(s: String): Variations {
            return Variations(a, b + s)
        }
    }

    private fun Set<String>.createVariations(): Set<Variations> {
        if (this.isEmpty()) {
            return setOf(Variations(setOf(), setOf()))
        }

        val el = this.toList().get(0)

        return (this - el).createVariations().flatMap {
            listOf(it.addToA(el), it.addToB(el))
        }.toSet()
    }

    private fun Map<String, Valve>.openValve(
        current: String,
        turnsLeft: Int,
        searchChain: Set<String> = setOf()
    ): OpenValve? {
        if (turnsLeft <= 1) {
            return null
        }

        val key = "$turnsLeft|$current|${searchChain.sorted().joinToString("")}"
        cache[key]?.let {
            return it.getOrNull()
        }

        val currentValve = this[current]!!
        val whenCurrentOpen = if (searchChain.contains(current)) {
            0
        } else {
            (turnsLeft - 1) * currentValve.rate
        }

        val allValues = mutableSetOf<OpenValve>()
        if (whenCurrentOpen > 0) {
            allValues.add(OpenValve(currentValve, turnsLeft - 1, whenCurrentOpen, null))
        }

        currentValve
            .connections
            .map { this[it]!! }
            .forEach { otherValve ->

                //excluding current
                openValve(otherValve.name, turnsLeft - 1, searchChain)?.let {
                    allValues.add(it)
                }

                if (whenCurrentOpen > 0) {
                    //including current
                    val nextStep = openValve(otherValve.name, turnsLeft - 2, searchChain + current)
                    val totalRate = whenCurrentOpen + (nextStep?.total ?: 0)
                    allValues.add(OpenValve(currentValve, turnsLeft - 1, totalRate, nextStep))
                }
            }

        val result = allValues
            .maxByOrNull { it.total }

        cache[key] = Optional.ofNullable(result)

        return result
    }

    fun run() {
        val lines = "day16.txt"
            .readFile()
            .lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val reg = "Valve ([A-Z]+) has flow rate=([\\d]+); tunnel[s]? lead[s]? to valve[s]? ([A-Z,\\s]+)".toRegex()

        val valves = lines
            .map { reg.find(it)!! }
            .map { p ->
                val (name, rate, connections) = p.destructured
                val conn = connections.replace(" ", "").split(",").toSet()
                name to Valve(name, rate.toInt(), conn)
            }
            .toMap()

        val valvesToOpen = valves.filter { v -> v.value.rate > 0 }
        val variations = valvesToOpen.map { it.key }.toSet().createVariations()

        println("Total var: ${variations.size}")

        fun Pair<OpenValve?, OpenValve?>.sum(): Int {
            return (first?.total ?: 0) + (second?.total ?: 0)
        }

        fun printVar(v: Variations, res: Pair<OpenValve?, OpenValve?>) {
            println()
            println("---------------------------")
            println("Total: ${res.sum()}")
            println("Vars: $v")
            println()
            println("First: ")
            var current: OpenValve? = res.first
            while (current != null) {
                println("valve=${current.valve.name} total=${current.total} startTurn=${26 - current.activeTurns}")
                current = current.next
            }
            println()

            println("Second: ")
            current = res.second
            while (current != null) {
                println("valve=${current.valve.name} total=${current.total} startTurn=${26 - current.activeTurns}")
                current = current.next
            }

            println()
            println()
        }

        fun Variations.calculate(): Pair<OpenValve?, OpenValve?> {
            val resultA = valves.openValve("AA", 26, a)
            val resultB = valves.openValve("AA", 26, b)
            return Pair(resultA, resultB)
        }

        val counter = AtomicInteger()

        val startMs = System.currentTimeMillis()

        val result = variations.parallelStream()
            .map {
                if (counter.incrementAndGet() % 100 == 0) {
                    println("(${((System.currentTimeMillis() - startMs)/100).toDouble() / 10}s) Handled ${counter.get().toDouble() / variations.size}% Cache: ${cache.size}")
                }
                it to it.calculate()
            }
            .toList()

        val (winV, winP) = result.maxBy { it.second.sum() }
        printVar(winV, winP)
    }

}

fun main() {
    Day16B.run()
}
