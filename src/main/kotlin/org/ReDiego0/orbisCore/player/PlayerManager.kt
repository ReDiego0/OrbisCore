package org.ReDiego0.orbisCore.modules.player

import org.ReDiego0.orbisCore.OrbisCore
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class PlayerManager(private val plugin: OrbisCore) {

    private val playerCache = ConcurrentHashMap<UUID, PlayerData>()
    private val dataFolder = File(plugin.dataFolder, "playerdata").apply { mkdirs() }

    fun getPlayerData(uuid: UUID): PlayerData? {
        return playerCache[uuid]
    }

    fun loadPlayer(uuid: UUID) {
        val file = File(dataFolder, "$uuid.yml")

        if (!file.exists()) {
            val newData = PlayerData(uuid)
            playerCache[uuid] = newData
            savePlayer(uuid) // Guardar el archivo inicial
            return
        }

        val config = YamlConfiguration.loadConfiguration(file)

        val data = PlayerData(
            uuid = uuid,
            className = config.getString("class", "NONE") ?: "NONE",
            level = config.getInt("level", 1),
            experience = config.getDouble("experience", 0.0),
            currentMana = config.getDouble("mana.current", 100.0),
            maxMana = config.getDouble("mana.max", 100.0)
        )
        playerCache[uuid] = data
    }

    fun saveAndRemovePlayer(uuid: UUID) {
        val data = playerCache.remove(uuid) ?: return
        saveToDisk(data)
    }

    fun savePlayer(uuid: UUID) {
        val data = playerCache[uuid] ?: return
        saveToDisk(data)
    }

    private fun saveToDisk(data: PlayerData) {
        val file = File(dataFolder, "${data.uuid}.yml")
        val config = YamlConfiguration()

        config.set("class", data.className)
        config.set("level", data.level)
        config.set("experience", data.experience)
        config.set("mana.current", data.currentMana)
        config.set("mana.max", data.maxMana)

        try {
            config.save(file)
        } catch (e: Exception) {
            plugin.logger.severe("Error guardando datos de ${data.uuid}: ${e.message}")
        }
    }

    fun saveAll() {
        playerCache.keys.forEach { savePlayer(it) }
    }
}