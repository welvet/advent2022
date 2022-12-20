import java.util.LinkedList

object Day20 {

    data class Node(
        val v: Long,
        var pos: Long,
        var left: Node? = null,
        var right: Node? = null
    ) {
        fun move(pc: Long) {
            var c = pc
            while (c != 0L) {
                val myOldLeft = left!!
                val myOldRight = right!!
                if (c > 0) {
                    val other = right!!
                    val otherRight = right!!.right!!

                    left = other
                    right = otherRight

                    other.left = myOldLeft
                    other.right = this

                    myOldLeft.right = other
                    otherRight.left = this

                    val myPos = pos
                    pos = other.pos
                    other.pos = myPos

                    c--
                } else {
                    val other = left!!
                    val otherLeft = left!!.left!!

                    left = otherLeft
                    right = other

                    other.left = this
                    other.right = myOldRight

                    myOldRight.left = other
                    otherLeft.right = this

                    val myPos = pos
                    pos = other.pos
                    other.pos = myPos

                    c++
                }
            }
        }

        fun findRoot(): Node {
            var current = this
            while (current.pos != 0L) {
                current = current.right!!
            }

            return current
        }

        fun findVal(t: Long): Node {
            var current = this
            while (current.v != t) {
                current = current.right!!
            }

            return current
        }

        fun withOffset(offset: Long): Node {
            var current = this
            for (i in 0 until offset) {
                current = current.right!!
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

        val queue = LinkedList<Node>()
        val size = nums.size.toLong()

        val root = Node(nums[0], 0)
        queue.add(root)

        var prev: Node = root
        for (i in 1 until nums.size) {
            val current = Node(nums[i], i.toLong())
            queue.add(current)

            current.left = prev
            prev.right = current

            prev = current
        }

        prev.right = root
        root.left = prev

        fun printList() {
            var current = root.findRoot()
            do {
                print("${current.v}, ")
                current = current.right!!
            } while (current.pos != 0L)
            println()
        }


        val loop: Long = (size) * (size - 1)
//        printList()
//        root.move(loop)
//        printList()

//        for (i in 0..100) {
//            root.move(1)
//            print("${i+1}: ")
//            printList()
//
//        }

        while (queue.isNotEmpty()) {
            val el = queue.removeFirst()
            el.move(el.v % loop)
        }

        val zero = root.findVal(0)
        var result = 0L
        for (i in listOf(1000, 2000, 3000)) {
            val v = zero.withOffset(i.toLong() % loop).v
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
    Day20.run()
}
