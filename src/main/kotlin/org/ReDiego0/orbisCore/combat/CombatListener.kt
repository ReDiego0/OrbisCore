package org.ReDiego0.orbisCore.combat

import org.ReDiego0.orbisCore.OrbisCore
import org.ReDiego0.orbisCore.config.SkillSlot
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

class CombatListener(private val plugin: OrbisCore, private val executor: SkillExecutor) : Listener {

    @EventHandler
    fun onDrop(event: PlayerDropItemEvent) {
        val player = event.player
        val data = plugin.playerManager.getPlayerData(player.uniqueId) ?: return

        val skillId = data.equippedSkills[SkillSlot.Q]

        if (skillId != null) {
            // Cancelamos el drop para que no tire su espada al suelo
            event.isCancelled = true
            executor.tryCast(player, skillId)
        }
    }

    @EventHandler
    fun onSwapHand(event: PlayerSwapHandItemsEvent) {
        val player = event.player
        val data = plugin.playerManager.getPlayerData(player.uniqueId) ?: return

        val skillId = data.equippedSkills[SkillSlot.F]

        if (skillId != null) {
            // Cancelamos el cambio de mano
            event.isCancelled = true

            executor.tryCast(player, skillId)
        }
    }
}