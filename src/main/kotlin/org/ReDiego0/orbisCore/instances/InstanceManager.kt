package org.ReDiego0.orbisCore.instances

import org.ReDiego0.orbisCore.OrbisCore
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class ActiveInstance(
    val instanceUuid: UUID = UUID.randomUUID(),
    val worldName: String,
    val partyId: UUID,
    val templateId: String,
    val creationTime: Long = System.currentTimeMillis()
)

class InstanceManager(private val plugin: OrbisCore) {

    private val worldManager = WorldManager(plugin)
    private val activeInstances = ConcurrentHashMap<UUID, ActiveInstance>() // InstanceUUID -> Data

    fun startInstance(partyId: UUID, templateName: String) {
        val instanceUuid = UUID.randomUUID()
        object : BukkitRunnable() {
            override fun run() {
                // La copia de archivos DEBERÍA ser asíncrona, pero Bukkit.createWorld DEBE ser síncrono.
                // TODO: Copiar async -> Volver al main thread -> createWorld.

                val world = worldManager.createInstanceWorld(templateName, instanceUuid.toString())

                if (world != null) {
                    val members = plugin.partyManager.getPartyMembers(partyId)
                    val spawnLoc = world.spawnLocation
                    object : BukkitRunnable() {
                        override fun run() {
                            members.forEach { uuid ->
                                Bukkit.getPlayer(uuid)?.teleport(spawnLoc)
                                Bukkit.getPlayer(uuid)?.sendMessage("§a¡Bienvenido a $templateName!")
                            }

                            val active = ActiveInstance(instanceUuid, world.name, partyId, templateName)
                            activeInstances[instanceUuid] = active

                            startExpirationTimer(instanceUuid)
                        }
                    }.runTask(plugin)
                }
            }
        }.runTaskAsynchronously(plugin)
    }

    fun endInstance(worldName: String) {
        val instance = activeInstances.values.find { it.worldName == worldName } ?: return

        activeInstances.remove(instance.instanceUuid)
        worldManager.unloadAndDeleteWorld(worldName)

        plugin.logger.info("Instancia ${instance.worldName} finalizada y borrada.")
    }

    private fun startExpirationTimer(id: UUID) {
        object : BukkitRunnable() {
            override fun run() {
                if (activeInstances.containsKey(id)) {
                    val inst = activeInstances[id]!!
                    endInstance(inst.worldName)
                }
            }
        }.runTaskLater(plugin, 20 * 60 * 60 * 3)
    }
}