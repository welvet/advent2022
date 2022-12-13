import java.util.LinkedList
import kotlin.math.abs

data class Context(var X: Int = 1)

class Action(private var cycles:Int, private val action: () -> Unit) {

    fun cycle(): Boolean {
        if (cycles-- == 0) {
            action()
            return true
        }

        return false
    }
}

enum class Instruction{
    ADDX {
        override fun createAction(s: String, context: Context): Action? {
            if (s.startsWith("addx")) {
                val value = s.split(" ")[1].toInt()
                return Action(1) {context.X += value}
            } else {
                return null
            }
        }
    }, NOOP {
        override fun createAction(s: String, context: Context): Action? {
            if (s.startsWith("noop")) {
                return Action(0) {}
            } else {
                return null
            }
        }
    };

    abstract fun createAction(s: String, context: Context): Action?

}

fun main(args: Array<String>) {
    val lines = "day10.txt"
        .readFile()
        .lines()
        .map { it.trim() }
        .filter { it.isNotBlank() }

    val context = Context()

    val actions = lines.map {
        Instruction.values()
            .map { i -> i.createAction(it, context) }
            .find { it != null }!!
    }

    val actionsQueue = LinkedList(actions)
    var currentAction: Action? = null
    var cycle = 0
    var screenCycle = -1

    while (actionsQueue.isNotEmpty() || currentAction != null) {
        cycle++
        screenCycle++

        if (abs(context.X - screenCycle) < 2) {
            print("#")
        } else {
            print(".")
        }

        if (cycle % 40 == 0) {
            screenCycle = -1
            println()
        }
        
        if (currentAction == null) {
            currentAction = actionsQueue.removeFirst()
        }
        if (currentAction!!.cycle()) {
            currentAction = null
        }

    }

}
