import java.util.LinkedList
import kotlin.math.floor

data class Monkey(
    val items: LinkedList<Item>,
    val denom: Int,
    val operation: (Long) -> Long,
    val test: (Long) -> Int,
    var count: Long
)

data class Item(var worryLevel: Long)

fun monkey(denom: Int, operation: (Long) -> Long, test: (Long) -> Int, vararg items: Long): Monkey {
    return Monkey(
        LinkedList(items.map { Item(it) }),
        denom,
        operation,
        test,
        0
    )
}

fun main(args: Array<String>) {
    val testSet = mapOf(
        0 to monkey(23, { it * 19 }, { if (it % 23 == 0L) 2 else 3 }, 79, 98),
        1 to monkey(19, { it + 6 }, { if (it % 19 == 0L) 2 else 0 }, 54, 65, 75, 74),
        2 to monkey(13, { it * it }, { if (it % 13 == 0L) 1 else 3 }, 79, 60, 97),
        3 to monkey(17, { it + 3 }, { if (it % 17 == 0L) 0 else 1 }, 74)
    )

    val actualSet = mapOf(
        0 to monkey(11, { it * 3 }, { if (it % 11 == 0L) 2 else 7 }, 50, 70, 54, 83, 52, 78),
        1 to monkey(7, { it * it }, { if (it % 7 == 0L) 0 else 2 }, 71, 52, 58, 60, 71),
        2 to monkey(3, { it + 1 }, { if (it % 3 == 0L) 7 else 5 }, 66, 56, 56, 94, 60, 86, 73),
        3 to monkey(5, { it + 8 }, { if (it % 5 == 0L) 6 else 4 }, 83, 99),
        4 to monkey(17, { it + 3 }, { if (it % 17 == 0L) 1 else 0 }, 98, 98, 79),
        5 to monkey(13, { it + 4 }, { if (it % 13 == 0L) 6 else 3 }, 76),
        6 to monkey(19, { it * 17 }, { if (it % 19 == 0L) 4 else 1 }, 52, 51, 84, 54),
        7 to monkey(2, { it + 7 }, { if (it % 2 == 0L) 5 else 3 }, 82, 86, 91, 79, 94, 92, 59, 94),
    )

    val set = actualSet
    var lc = 1
    set.values.map { it.denom }.forEach {
        lc *= it
    }

    for (round in 0 until 10000) {
        for (mid in 0 until set.size) {
            val monkey = set[mid]!!
            while (monkey.items.isNotEmpty()) {
                val item = monkey.items.removeFirst()
                item.worryLevel = monkey.operation(item.worryLevel)
//                item.worryLevel = floor(item.worryLevel.toDouble() / 3).toLong()
                item.worryLevel = item.worryLevel % lc;
                val nextMonkeyId = monkey.test(item.worryLevel)
                monkey.count++

                set[nextMonkeyId]!!.items.add(item)
            }
        }
    }

    val (a, b) = set.values
        .map { it.count }
        .sorted()
        .reversed()
        .take(2)


    println(a * b)
}
