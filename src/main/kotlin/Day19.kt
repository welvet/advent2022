import Day19.Resource.*

object Day19 {

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
        fun qualityLevel(): Int {
            val l = findLevel()
            println("Blueprint $id level is $l total ${id * l}")
            return id * l
        }

        private fun findLevel(): Int {
            var operations = listOf(
                Operation(this, Resources(), Resources(mapOf(ORE to 1)), null)
            )

            for (minute in 0 until 24) {
                println("$id: min=$minute size=${operations.size}")
                operations = operations
                    .flatMap { it.createBranches(minute) }
                    .distinct()
                    .map { it.collectResources() }
                    .map { it.buildRobot() }
            }

            return operations.maxOf { it.resources[GEODE] }
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

        fun build(resources: Resources): Resources {
            val newResources = resources.copy()

            Resource.values().forEach { res ->
                newResources[res] = resources[res] - cost[res]
                check(newResources[res] >= 0)
            }

            return newResources
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
        val resources: Resources,
        val robots: Resources,
        val robotToBuild: Resource?
    ) {
        fun collectResources(): Operation {
            val newResources = resources.copy()

            Resource.values().forEach { res ->
                newResources[res] = resources[res] + robots[res]
            }


            return this.copy(resources = newResources)
        }

        fun createBranches(minute: Int): List<Operation> {
            val result = mutableListOf<Operation>()
            result.add(this)

            val canBuildRobots = mutableSetOf<Robot>()
            if (blueprint.geodeRobot.canBuild(resources)) {
                canBuildRobots.add(blueprint.geodeRobot)
            } else if (blueprint.obsidianRobot.canBuild(resources)) {
                canBuildRobots.add(blueprint.obsidianRobot)
            } else {
                if (blueprint.oreRobot.canBuild(resources)) {
                    canBuildRobots.add(blueprint.oreRobot)
                }
                if (blueprint.clayRobot.canBuild(resources)) {
                    canBuildRobots.add(blueprint.clayRobot)
                }
            }

            canBuildRobots
                .forEach { robot ->
                    val newResources = robot.build(resources)
                    val newRobots = robots.copy()
                    newRobots[robot.type] = newRobots[robot.type] + 1

                    result.add(this.copy(resources = newResources, robotToBuild = robot.type))
                }

            return result
        }

        fun buildRobot(): Operation {
            if (robotToBuild == null) {
                return this
            }

            val newRobots = robots.copy()
            newRobots[robotToBuild] += 1

            return this.copy(robots = newRobots, robotToBuild = null)
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

        val result = prints
            .parallelStream()
            .map { it.qualityLevel() }
            .toList()
            .sum()
        println("R: $result")
    }

}

fun main() {
    Day19.run()
}
