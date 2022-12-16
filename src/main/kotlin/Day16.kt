import java.util.*

object Day16 {

    data class Valve(
        val name: String,
        val rate: Int,
        val connections: Set<String>
    )

    data class OpenValve(
        val valve: Valve,
        val activeTurns: Int,
        val total: Int,
        val next: OpenValve?
    )


    private val cache: MutableMap<Triple<String, Int, Set<String>>, OpenValve> = mutableMapOf()

    private fun Map<String, Valve>.openValve(
        current: String,
        turnsLeft: Int,
        searchChain: Set<String> = setOf()
    ): OpenValve? {
        if (turnsLeft <= 1) {
            return null
        }

        val key = Triple(current, turnsLeft, searchChain)
        cache[key]?.let {
            return it
        }

        val currentValve = this[current]!!
        val whenCurrentOpen = if (searchChain.contains(current)) {
            0
        } else {
            (turnsLeft - 1) * currentValve.rate
        }

        val myValue = mutableListOf<OpenValve>()
        if (whenCurrentOpen > 0) {
            myValue.add(OpenValve(currentValve, turnsLeft - 1, whenCurrentOpen, null))
        }

        val includingOthers = currentValve
            .connections
            .map { this[it]!! }
            .flatMap { otherValve ->
                val result = mutableListOf<OpenValve>()
                //excluding current
                openValve(otherValve.name, turnsLeft - 1, searchChain)?.let {
//                    println("($turnsLeft) $current -> ${otherValve.name}: ${it.total}")
                    result.add(it)
                }

                if (whenCurrentOpen > 0) {
                    //including current
                    val nextStep = openValve(otherValve.name, turnsLeft - 2, searchChain + setOf(current))
                    val totalRate = whenCurrentOpen + (nextStep?.total ?: 0)
                    result.add(OpenValve(currentValve, turnsLeft - 1, totalRate, nextStep))
//                println("($turnsLeft)+ $current -> ${otherValve.name}: $totalRate")
                }

                result
            }

        val result = (myValue + includingOthers)
            .maxByOrNull { it.total }

        result?.let { cache[key] = it }

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


        val turns = 30
        val result = valves.openValve("AA", turns)
        println(result!!.total)
        println()

        var current: OpenValve? = result
        while (current != null) {
            println("valve=${current.valve.name} total=${current.total} startTurn=${turns - current.activeTurns}")
            current = current.next
        }
    }

}

fun main() {
    Day16B.run()
}
