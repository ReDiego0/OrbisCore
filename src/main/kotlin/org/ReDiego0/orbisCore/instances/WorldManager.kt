package org.ReDiego0.orbisCore.instances

import org.ReDiego0.orbisCore.OrbisCore
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import java.io.File

class WorldManager(private val plugin: OrbisCore) {

    private val templatesFolder = File(plugin.server.worldContainer, "templates")
    private val instancesFolder = plugin.server.worldContainer
    fun copyWorldFiles(templateName: String, instanceId: String): Boolean {
        val source = File(templatesFolder, templateName)
        val target = File(instancesFolder, "inst_$instanceId")

        if (!source.exists()) {
            plugin.logger.severe("Template '$templateName' no existe en /templates/")
            return false
        }

        return try {
            source.copyRecursively(target, overwrite = true)
            File(target, "uid.dat").delete()
            true
        } catch (e: Exception) {
            plugin.logger.severe("Error copiando archivos de instancia: ${e.message}")
            e.printStackTrace()
            false
        }
    }


    fun loadInstanceWorld(instanceId: String): World? {
        val creator = WorldCreator("inst_$instanceId")
        // Desactivar guardado automático para mejorar rendimiento
        // creator.generator(EmptyChunkGenerator()) // Si tuvieras uno vacío
        val world = plugin.server.createWorld(creator)
        world?.isAutoSave = false
        return world
    }

    fun unloadAndDeleteWorld(worldName: String) {
        val world = Bukkit.getWorld(worldName)
        if (world != null) {
            val fallback = Bukkit.getWorlds()[0].spawnLocation
            world.players.forEach { it.teleport(fallback) }

            plugin.server.unloadWorld(world, false)
        }

        val dir = File(instancesFolder, worldName)
        if (dir.exists() && dir.name.startsWith("inst_")) {
            dir.deleteRecursively()
        }
    }
}