import java.util.LinkedList

object Day20B {

    var X = ArrayList<Node>()

    fun printList() {
        X.forEach {
            print("${it.v}, ")
        }
        println()
    }

    data class Node(
        val v: Long,
        var pos: Int
    ) {
        fun move(pc: Int) {
            var c = pc
            while (c != 0) {
                val myPos = pos
                val otherPos: Int

                if (c > 0) {
                    otherPos = rightPos()
                    c--

                } else {
                    otherPos = leftPos()
                    c++
                }

                if (myPos == X.size - 1 && otherPos == 0) {
                    var prev: Node = this
                    for (i in 0 until X.size ) {
                        val curr = X[i]
                        X[i] = prev
                        X[i].pos = i

                        prev = curr
                    }
                    c++
                } else if (myPos == 0 && otherPos == X.size - 1) {
                    for (i in 1 until X.size ) {
                        val curr = X[i]
                        X[i - 1] = curr
                        X[i - 1].pos = i - 1
                    }
                    X[X.size - 1] = this
                    pos = X.size - 1
                    c--
                } else {
                    val otherNode = X[otherPos]

                    X[otherPos] = this
                    this.pos = otherPos

                    X[myPos] = otherNode
                    otherNode.pos = myPos
                }
            }
        }

        private fun leftPos() = if (pos - 1 >= 0) pos - 1 else X.size - 1

        private fun rightPos() = (pos + 1) % X.size

        fun findVal(t: Long): Node {
            var current = this
            while (current.v != t) {
                current = X[current.rightPos()]
            }

            return current
        }

        fun withOffset(offset: Long): Node {
            var current = this
            for (i in 0 until offset) {
                current = X[current.rightPos()]
            }

            return current
        }

        override fun toString(): String {
            return "Node(v=$v, pos=$pos)"
        }

    }

    fun run() {
        val nums = "day20.txt"
            .readFile()
            .lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .map { it.toLong() }
            .map { it * 811589153  }

        val queue = LinkedList<Node>()
        val size = nums.size.toLong()

        val root = Node(nums[0], 0)
        queue.add(root)
        X.add(root)

        for (i in 1 until nums.size) {
            val current = Node(nums[i], i)
            queue.add(current)
            X.add(current)
        }

        val queueCopy = ArrayList(queue)
        repeat(9) {
            queue.addAll(queueCopy)
        }

        val loop: Long = (size - 1)
        printList()

        while (queue.isNotEmpty()) {
            val el = queue.removeFirst()
            el.move((el.v % loop).toInt())
//            print("Move ${el.v}: ")
//            printList()
            println("Queue size = ${queue.size}")
        }

        val zero = root.findVal(0)
        var result = 0L
        for (i in listOf(1000, 2000, 3000)) {
            val v = zero.withOffset(i.toLong()).v
            println("V=$v")
            result += v
        }

        /*
        V=6801
        V=9840
        V=849
        Res = 17490
         */
        println("Res = $result")
    }

}

fun main() {
    Day20B.run()
}
