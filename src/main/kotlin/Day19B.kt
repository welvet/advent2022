import Day19B.Resource.*
import java.util.concurrent.ConcurrentHashMap

object Day19B {

    class Resources(
        val data: IntArray = IntArray(4)
    ) {

        constructor(m: Map<Resource, Int>) : this() {
            m.forEach { (k, v) ->
                data[k.id] = v
            }
        }

        operator fun get(r: Resource): Int {
            return data[r.id]
        }

        operator fun set(r: Resource, v: Int) {
            data[r.id] = v
        }

        fun copy(): Resources {
            return Resources(data.copyOf())
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Resources

            if (!data.contentEquals(other.data)) return false

            return true
        }

        override fun hashCode(): Int {
            return data.contentHashCode()
        }
    }

    enum class Resource(val id: Int) {
        ORE(0), CLAY(1), OBSIDIAN(2), GEODE(3)
    }

    data class Blueprint(
        val id: Int,
        val oreRobot: Robot,
        val clayRobot: Robot,
        val obsidianRobot: Robot,
        val geodeRobot: Robot
    ) {
        val robots = listOf(oreRobot, clayRobot, obsidianRobot, geodeRobot)

        fun qualityLevel(): Int {
            val l = findLevel()
            println("Blueprint $id level is $l")
            return l
        }

        private fun findLevel(): Int {
            var operations = ConcurrentHashMap.newKeySet<Operation>()

            operations.add(Operation(this, Resources(), Resources(mapOf(ORE to 1)), null))

            val maxRobots = mutableMapOf(-1 to 0)
            val totalMinutes = 32

            for (minute in 0 until totalMinutes) {
                val maxGeode = operations.maxOfOrNull { it.resources[GEODE] }
                val maxRobot = operations.maxOfOrNull { it.robots[GEODE] }
                maxRobots[minute] = maxRobot ?: 0

                println("$id: min=$minute size=${operations.size} geode=$maxGeode robot=$maxRobot")

                val newOperations = ConcurrentHashMap.newKeySet<Operation>(operations.size * 4)

                operations.parallelStream().forEach {
                    it.createBranches(minute + 1 == totalMinutes).forEach { o ->
                        o.collectResources()
                        o.buildRobot()

                        if (shouldInclude(o, minute, maxRobots)) {
                            newOperations.add(o)
                        }
                    }
                }

                operations = newOperations
            }

            return operations.maxOf { it.resources[GEODE] }
        }

        fun shouldInclude(operation: Operation, currentTurn: Int, maxRobot: Map<Int, Int>): Boolean {
            return operation.robots[GEODE] >= (maxRobot[currentTurn - 2] ?: 0)
        }
    }

    data class Robot(
        val type: Resource,
        val cost: Resources
    ) {
        fun canBuild(resources: Resources): Boolean {
            Resource.values().forEach { res ->
                if (cost[res] > resources[res]) {
                    return false
                }
            }

            return true
        }

        fun build(resources: Resources) {
            Resource.values().forEach { res ->
                resources[res] = resources[res] - cost[res]
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Robot

            if (type != other.type) return false

            return true
        }

        override fun hashCode(): Int {
            return type.hashCode()
        }


    }

    data class Operation(
        val blueprint: Blueprint,
        var resources: Resources,
        var robots: Resources,
        var robotToBuild: Resource?
    ) {
        fun collectResources() {
            Resource.values().forEach { res ->
                resources[res] = resources[res] + robots[res]
            }
        }

        private fun MutableList<Operation>.addCopy(robot: Robot) {
            val maxCost = blueprint.robots.maxOf { it.cost[robot.type] }
            val currentAmount = robots[robot.type]

            if (robot.type != GEODE && currentAmount >= maxCost) {
                return
            }

            val newResources = resources.copy()
            robot.build(newResources)

            this.add(
                this@Operation.copy(
                    robots = robots.copy(),
                    resources = newResources,
                    robotToBuild = robot.type
                )
            )
        }

        fun createBranches(lastMinute: Boolean): List<Operation> {
            val destination = mutableListOf<Operation>()

            destination.add(this)

            if (!lastMinute) {
                if (blueprint.geodeRobot.canBuild(resources)) {
                    destination.addCopy(blueprint.geodeRobot)
                }
                if (blueprint.obsidianRobot.canBuild(resources)) {
                    destination.addCopy(blueprint.obsidianRobot)
                }
                if (blueprint.oreRobot.canBuild(resources)) {
                    destination.addCopy(blueprint.oreRobot)
                }
                if (blueprint.clayRobot.canBuild(resources)) {
                    destination.addCopy(blueprint.clayRobot)
                }
            }

            return destination
        }

        fun buildRobot() {
            if (robotToBuild != null) {
                robots[robotToBuild!!] += 1
                robotToBuild = null
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Operation

            if (resources != other.resources) return false
            if (robots != other.robots) return false
            if (robotToBuild != other.robotToBuild) return false

            return true
        }

        override fun hashCode(): Int {
            var result = resources.hashCode()
            result = 31 * result + robots.hashCode()
            result = 31 * result + (robotToBuild?.hashCode() ?: 0)
            return result
        }

    }

    fun run() {
        val lines = "day19.txt"
            .readFile()
            .lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val r =
            "Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.".toRegex()

        val prints = lines
            .map { s -> r.find(s)!! }
            .map { r ->
                val values = r.destructured.toList().map { it.toInt() }

                val id = values[0]
                val oreOre = values[1]
                val clayOre = values[2]
                val obsidianOre = values[3]
                val obsidianClay = values[4]
                val geodeOre = values[5]
                val geodeObsidian = values[6]

                Blueprint(
                    id,
                    Robot(ORE, Resources(mapOf(ORE to oreOre))),
                    Robot(CLAY, Resources(mapOf(ORE to clayOre))),
                    Robot(OBSIDIAN, Resources(mapOf(ORE to obsidianOre, CLAY to obsidianClay))),
                    Robot(GEODE, Resources(mapOf(ORE to geodeOre, OBSIDIAN to geodeObsidian))),
                )
            }

        val ms = System.currentTimeMillis()

        val results = prints
            .take(3)
            .map { it.qualityLevel() }
            .toList()

        var result = 1
        results.forEach { result *= it }

        println("R: $result time: ${System.currentTimeMillis() - ms}ms")
    }

}

fun main() {
    Day19B.run()
}
