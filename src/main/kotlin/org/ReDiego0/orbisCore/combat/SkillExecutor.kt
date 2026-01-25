package org.ReDiego0.orbisCore.combat

import io.lumine.mythic.bukkit.MythicBukkit
import net.kyori.adventure.text.Component
import org.ReDiego0.orbisCore.OrbisCore
import org.bukkit.Sound
import org.bukkit.entity.Player

class SkillExecutor(private val plugin: OrbisCore) {

    private val cooldownManager = CooldownManager()

    fun tryCast(player: Player, skillId: String) {
        val uuid = player.uniqueId
        val data = plugin.playerManager.getPlayerData(uuid) ?: return
        val classInfo = plugin.classRegistry.getClass(data.className) ?: return
        val skillInfo = classInfo.skills[skillId] ?: return
        val manaCost = skillInfo.manaCost
        val cooldownSeconds = skillInfo.cooldown

        if (cooldownManager.isOnCooldown(uuid, skillId)) {
            val left = String.format("%.1f", cooldownManager.getRemainingSeconds(uuid, skillId))
            player.sendActionBar(Component.text("§cHabilidad en enfriamiento: ${left}s"))
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f)
            return
        }

        if (data.currentMana < manaCost) {
            player.sendActionBar(Component.text("§c¡No tienes suficiente Éter! (§b${data.currentMana.toInt()}/${manaCost.toInt()}§c)"))
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f)
            return
        }

        val skillManager = MythicBukkit.inst().skillManager
        if (!skillManager.getSkill(skillInfo.mmSkill).isPresent) {
            player.sendMessage("§cError: La habilidad interna '${skillInfo.mmSkill}' no existe.")
            plugin.logger.warning("Configuración errónea: Skill '${skillInfo.mmSkill}' no encontrada en MythicMobs.")
            return
        }

        val success = MythicBukkit.inst().apiHelper.castSkill(player, skillInfo.mmSkill)

        if (success) {
            data.currentMana -= manaCost
            cooldownManager.setCooldown(uuid, skillId, cooldownSeconds)
            plugin.logger.info("Jugador ${player.name} usó ${skillInfo.id} (Cost: $manaCost, CD: ${cooldownSeconds}s)")
        } else {
            player.sendMessage("§cLa habilidad falló al activarse.")
        }
    }
}