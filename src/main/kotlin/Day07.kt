import java.util.*

private data class Dir(
    val name: String,
    val dirs: MutableMap<String, Dir> = mutableMapOf(),
    val files: MutableMap<String, Int> = mutableMapOf(),
    private var size: Int = -1
) {
    fun dir(name: String): Dir {
        return dirs.getOrPut(name) { Dir(name) }
    }

    fun file(name: String, size: Int) {
        files[name] = size
    }

    fun size(): Int {
        if (size == -1) {
            size = files.values.sum() + dirs.values.sumOf { it.size() }
        }

        return size
    }

    fun nestedChildren(): List<Dir> {
        return dirs.values.flatMap {
            mutableListOf(it) + it.nestedChildren()
        }
    }
}

fun main(args: Array<String>) {
    val lines = "day07.txt".readFile().lines()

    val root = Dir("/")

    val path = Stack<Dir>()
    path.push(root)

    val commands = lines
        .filter { it.isNotBlank() }
        .map { it.trim() }
        .split { it.startsWith("$") }

    commands.forEach { block ->
        val cmd = block[0]
        val currentDir = path.peek()

        when {
            cmd.startsWith("$ cd") -> {
                when (val dir = cmd.replace("$ cd ", "")) {
                    "/" -> {
                        path.clear()
                        path.push(root)
                    }

                    ".." -> path.pop()
                    else -> path.push(currentDir.dir(dir))
                }
            }

            else -> {
                block
                    .drop(1)
                    .forEach {
                        when {
                            it.startsWith("dir ") -> {
                                val dir = it.replace("dir ", "")
                                currentDir.dir(dir)
                            }

                            else -> {
                                val (size, name) = it.split(" ")
                                currentDir.file(name, size.toInt())
                            }
                        }
                    }
            }
        }
    }

    val sizeToRemove = 30000000 - (70000000 - root.size())

    val dirToRemove = root
        .nestedChildren()
        .sortedBy { it.size() }
        .find { it.size() >= sizeToRemove }

    println(dirToRemove!!.size())
}



