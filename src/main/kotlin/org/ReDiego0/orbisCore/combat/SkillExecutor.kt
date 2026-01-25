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

        // TODO: Mover estos valores al YAML de la skill en el futuro
        val manaCost = 20.0
        val cooldownSeconds = 2.0

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

        if (!MythicBukkit.inst().skillManager.getSkill(skillInfo.mmSkill).isPresent) {
            player.sendMessage("§cError: La habilidad '${skillInfo.mmSkill}' no existe en MythicMobs.")
            plugin.logger.warning("La skill ${skillInfo.mmSkill} configurada en classes.yml no existe en la carpeta de MythicMobs.")
            return
        }

        val success = MythicBukkit.inst().apiHelper.castSkill(player, skillInfo.mmSkill)

        if (success) {
            data.currentMana -= manaCost
            cooldownManager.setCooldown(uuid, skillId, cooldownSeconds)

            plugin.logger.info("Jugador ${player.name} lanzó ${skillInfo.mmSkill}")
        } else {
            player.sendMessage("§cLa habilidad falló al activarse.")
        }
    }
}