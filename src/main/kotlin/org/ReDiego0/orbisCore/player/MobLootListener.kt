package org.ReDiego0.orbisCore.player

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent
import net.kyori.adventure.text.Component

import org.ReDiego0.orbisCore.OrbisCore
import org.ReDiego0.orbisCore.instances.InstanceInfo
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

import java.io.File
import kotlin.random.Random

class MobLootListener(private val plugin: OrbisCore) : Listener {
    data class MobConfig(val xp: Double, val lootTier: String)
    private val mobConfigMap = HashMap<String, MobConfig>()

    init {
        loadMobConfig()
    }

    fun loadMobConfig() {
        val file = File(plugin.dataFolder, "mobs.yml")
        if (!file.exists()) plugin.saveResource("mobs.yml", false)

        val config = YamlConfiguration.loadConfiguration(file)
        mobConfigMap.clear()

        val section = config.getConfigurationSection("mobs") ?: return

        for (key in section.getKeys(false)) {
            if (config.isDouble("mobs.$key")) {
                mobConfigMap[key] = MobConfig(config.getDouble("mobs.$key"), "BASIC")
            } else {
                val xp = config.getDouble("mobs.$key.xp", 0.0)
                val tier = config.getString("mobs.$key.loot_tier", "BASIC")!!
                mobConfigMap[key] = MobConfig(xp, tier)
            }
        }
        plugin.logger.info("Cargados ${mobConfigMap.size} mobs con XP y Loot Tiers.")
    }

    @EventHandler
    fun onMythicMobDeath(event: MythicMobDeathEvent) {
        if (event.killer !is Player) return

        val player = event.killer as Player
        val mobName = event.mobType.internalName
        val worldName = player.world.name
        val config = mobConfigMap[mobName] ?: return // Si no está en mobs.yml, ignoramos
        if (config.xp > 0) {
            val data = plugin.playerManager.getPlayerData(player.uniqueId)
            if (data != null) {
                data.addExperience(config.xp)
                player.sendActionBar(Component.text("§a+${config.xp.toInt()} XP"))
            }
        }

        val instanceInfo = plugin.instanceRegistry.getInstanceByWorld(worldName)

        if (instanceInfo != null) {
            processLootTable(instanceInfo, config.lootTier, event.entity.location)
        }
    }

    private fun processLootTable(instance: InstanceInfo, tableId: String, location: Location) {
        val table = instance.tables[tableId] ?: return
        if (Random.nextDouble() > table.categoryChance) return
        for (lootItem in table.items) {
            if (Random.nextDouble() <= lootItem.chance) {

                val amount = Random.nextInt(lootItem.minAmount, lootItem.maxAmount + 1)
                val itemStack = plugin.itemProvider.getItem(lootItem.id, amount)

                if (itemStack != null) {
                    location.world.dropItemNaturally(location, itemStack)

                    if (lootItem.chance <= 0.1) {
                        location.world.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 2.0f)
                    }
                }
            }
        }
    }
}