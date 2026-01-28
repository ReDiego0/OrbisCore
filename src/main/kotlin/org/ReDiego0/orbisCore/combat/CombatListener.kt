package org.ReDiego0.orbisCore.combat

import net.kyori.adventure.text.Component
import org.ReDiego0.orbisCore.OrbisCore
import org.ReDiego0.orbisCore.config.SkillSlot
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

class CombatListener(private val plugin: OrbisCore) : Listener {
    @EventHandler
    fun onDrop(event: PlayerDropItemEvent) {
        val player = event.player
        val data = plugin.playerManager.getPlayerData(player.uniqueId) ?: return
        if (data.equippedSkills[SkillSlot.Q] == null) return

        event.isCancelled = true

        plugin.skillManager.castSlot(player, SkillSlot.Q)
    }

    @EventHandler
    fun onSwapHand(event: PlayerSwapHandItemsEvent) {
        val player = event.player
        val data = plugin.playerManager.getPlayerData(player.uniqueId) ?: return
        if (data.equippedSkills[SkillSlot.F] == null) return

        event.isCancelled = true

        val item = player.inventory.itemInMainHand
        if (item.type == Material.AIR) {
            player.sendActionBar(Component.text("§c¡Necesitas un arma para canalizar Éter!"))
            player.playSound(player.location, Sound.BLOCK_CHAIN_HIT, 1f, 2f)
            return
        }

        plugin.skillManager.castSlot(player, SkillSlot.F)
    }
}