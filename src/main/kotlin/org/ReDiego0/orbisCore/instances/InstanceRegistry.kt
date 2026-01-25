package org.ReDiego0.orbisCore.instances

import org.ReDiego0.orbisCore.OrbisCore
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.logging.Level

data class LootItem(
    val id: String,
    val chance: Double,
    val minAmount: Int,
    val maxAmount: Int
)

data class LootTable(
    val id: String, // BASIC, IMPORTANT, DECISIVE
    val categoryChance: Double,
    val items: List<LootItem>
)

data class InstanceInfo(
    val id: String,
    val worldName: String,
    val tables: Map<String, LootTable>
)

class InstanceRegistry(private val plugin: OrbisCore) {

    private val worldInstanceMap = HashMap<String, InstanceInfo>()

    fun loadInstances() {
        val file = File(plugin.dataFolder, "instances.yml")
        if (!file.exists()) plugin.saveResource("instances.yml", false)

        val config = YamlConfiguration.loadConfiguration(file)
        worldInstanceMap.clear()

        val section = config.getConfigurationSection("instances") ?: return

        for (key in section.getKeys(false)) {
            try {
                val path = "instances.$key"
                val worldName = config.getString("$path.world_name") ?: continue
                val tablesMap = HashMap<String, LootTable>()
                val tablesSection = config.getConfigurationSection("$path.loot_tables")

                if (tablesSection != null) {
                    for (tableId in tablesSection.getKeys(false)) {
                        val tPath = "$path.loot_tables.$tableId"
                        val catChance = config.getDouble("$tPath.category_chance", 0.0)

                        val itemsList = ArrayList<LootItem>()
                        val rawItems = config.getMapList("$tPath.items")

                        for (rawItem in rawItems) {
                            val map = rawItem as Map<String, Any>
                            itemsList.add(LootItem(
                                id = map["id"] as String,
                                chance = (map["chance"] as Number).toDouble(),
                                minAmount = (map["min"] as? Number)?.toInt() ?: 1,
                                maxAmount = (map["max"] as? Number)?.toInt() ?: 1
                            ))
                        }

                        tablesMap[tableId] = LootTable(tableId, catChance, itemsList)
                    }
                }

                val info = InstanceInfo(key, worldName, tablesMap)
                worldInstanceMap[worldName] = info

            } catch (e: Exception) {
                plugin.logger.log(Level.SEVERE, "Error cargando instancia $key", e)
            }
        }
        plugin.logger.info("Cargadas ${worldInstanceMap.size} Instancias con sus tablas de loot.")
    }

    fun getInstanceByWorld(worldName: String): InstanceInfo? {
        return worldInstanceMap[worldName]
    }
}