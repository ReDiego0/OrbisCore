package org.ReDiego0.orbisCore.instances

import org.ReDiego0.orbisCore.OrbisCore
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class ActiveInstance(
    val instanceUuid: UUID,
    val worldName: String,
    val partyId: UUID,
    val templateId: String,
    val creationTime: Long = System.currentTimeMillis()
)

class InstanceManager(private val plugin: OrbisCore) {

    private val worldManager = WorldManager(plugin)
    private val activeInstances = ConcurrentHashMap<UUID, ActiveInstance>()

    fun startInstance(partyId: UUID, templateName: String, onStartCommand: String? = null) {
        val instanceUuid = UUID.randomUUID()
        val instanceIdStr = instanceUuid.toString()
        object : BukkitRunnable() {
            override fun run() {
                val copySuccess = worldManager.copyWorldFiles(templateName, instanceIdStr)

                if (!copySuccess) {
                    plugin.logger.severe("Fallo al copiar archivos para la instancia $instanceIdStr")
                    return
                }

                object : BukkitRunnable() {
                    override fun run() {
                        val world = worldManager.loadInstanceWorld(instanceIdStr)

                        if (world != null) {
                            val members = plugin.partyManager.getPartyMembers(partyId)
                            val spawnLoc = world.spawnLocation

                            members.forEach { uuid ->
                                val player = Bukkit.getPlayer(uuid)
                                if (player != null) {
                                    player.teleport(spawnLoc)
                                    player.sendMessage("§a¡Bienvenido a la instancia $templateName!")

                                    if (onStartCommand != null) {
                                        val cmd = onStartCommand.replace("{player}", player.name)
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd)
                                    }
                                }
                            }

                            val active = ActiveInstance(instanceUuid, world.name, partyId, templateName)
                            activeInstances[instanceUuid] = active
                            startExpirationTimer(instanceUuid)

                            plugin.logger.info("Instancia creada: ${world.name} (Template: $templateName)")
                        } else {
                            plugin.logger.severe("Bukkit falló al cargar el mundo inst_$instanceIdStr")
                        }
                    }
                }.runTask(plugin)
            }
        }.runTaskAsynchronously(plugin)
    }

    fun endInstance(worldName: String) {
        val instance = activeInstances.values.find { it.worldName == worldName } ?: return
        activeInstances.remove(instance.instanceUuid)

        val world = Bukkit.getWorld(worldName)
        if (world != null) {
            val fallback = Bukkit.getWorlds()[0].spawnLocation

            ArrayList(world.players).forEach { player ->
                player.teleport(fallback)
                player.sendMessage("§eLa instancia ha finalizado.")
            }
        }

        object : BukkitRunnable() {
            override fun run() {
                worldManager.unloadAndDeleteWorld(worldName)
                plugin.logger.info("Instancia ${instance.worldName} eliminada correctamente.")
            }
        }.runTaskLater(plugin, 20L)
    }

    private fun startExpirationTimer(id: UUID) {
        object : BukkitRunnable() {
            override fun run() {
                if (activeInstances.containsKey(id)) {
                    val inst = activeInstances[id]!!
                    endInstance(inst.worldName)
                }
            }
        }.runTaskLater(plugin, 20 * 60 * 60 * 3) // 3 Horas
    }

    fun cleanupAll() {
        for (inst in activeInstances.values) {
            worldManager.unloadAndDeleteWorld(inst.worldName)
        }
        activeInstances.clear()
    }
}