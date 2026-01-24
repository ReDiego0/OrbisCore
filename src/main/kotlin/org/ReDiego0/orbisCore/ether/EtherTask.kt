package org.ReDiego0.orbisCore.ether

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.ReDiego0.orbisCore.OrbisCore
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.min

class EtherTask(private val plugin: OrbisCore) : BukkitRunnable() {

    override fun run() {
        for (player in Bukkit.getOnlinePlayers()) {
            val uuid = player.uniqueId
            val data = plugin.playerManager.getPlayerData(uuid) ?: continue

            val classInfo = plugin.classRegistry.getClass(data.className)

            if (classInfo == null) {
                // TODO: Funcionalidad de elegir clase"
                continue
            }

            if (data.currentMana < data.maxMana) {
                val regenAmount = classInfo.manaRegen
                data.currentMana = min(data.currentMana + regenAmount, data.maxMana)
            }

            val health = player.health.toInt()
            val maxHealth = player.getAttribute(Attribute.MAX_HEALTH)?.value?.toInt() ?: 20
            val mana = data.currentMana.toInt()
            val maxMana = data.maxMana.toInt()

            val message = "§c❤ $health/$maxHealth   §b⚡ $mana/$maxMana"
            player.sendActionBar(LegacyComponentSerializer.legacySection().deserialize(message))
        }
    }
}