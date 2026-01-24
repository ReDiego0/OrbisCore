package org.ReDiego0.orbisCore.combat

import net.kyori.adventure.text.Component
import org.ReDiego0.orbisCore.OrbisCore
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player

class SkillExecutor(private val plugin: OrbisCore) {

    private val cooldownManager = CooldownManager()

    fun tryCast(player: Player, skillId: String) {
        val uuid = player.uniqueId
        val data = plugin.playerManager.getPlayerData(uuid) ?: return

        val classInfo = plugin.classRegistry.getClass(data.className) ?: return
        val skillInfo = classInfo.skills[skillId] ?: return

        // TODO: En el futuro, el costo de maná y cooldown podrían venir del YAML de la skill.
        val manaCost = 20.0
        val cooldownSeconds = 5.0

        if (cooldownManager.isOnCooldown(uuid, skillId)) {
            val left = String.format("%.1f", cooldownManager.getRemainingSeconds(uuid, skillId))
            player.sendActionBar(Component.text("§cHabilidad en enfriamiento: ${left}s"))
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f)
            return
        }

        if (data.currentMana < manaCost) {
            player.sendActionBar(Component.text("§c¡No tienes suficiente Éter!"))
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f)
            return
        }

        data.currentMana -= manaCost
        cooldownManager.setCooldown(uuid, skillId, cooldownSeconds)

        // Ejecutar MythicMobs
        // Usamos dispatchCommand para máxima compatibilidad sin depender del .jar en local
        // Formato: mm cast <SkillName> <Target>
        val mmCommand = "mm cast ${skillInfo.mmSkill} @self"
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at ${player.name} run $mmCommand")

        // C. Feedback Visual (Opcional)
        // player.sendMessage("§a¡Lanzaste ${skillInfo.displayName}!")
    }
}