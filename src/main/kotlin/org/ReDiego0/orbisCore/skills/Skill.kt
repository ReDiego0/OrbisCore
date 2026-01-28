package org.ReDiego0.orbisCore.skills

import org.ReDiego0.orbisCore.OrbisCore
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import net.kyori.adventure.text.Component

abstract class Skill(
    protected val plugin: OrbisCore,
    val id: String,
    val type: SkillType
) {

    var displayName: String = "Habilidad Desconocida"
    var cooldownSeconds: Double = 0.0
    var manaCost: Double = 0.0
    var minLevel: Int = 1

    open fun loadConfig(config: FileConfiguration, classId: String) {
        val path = "classes.$classId.available_skills.$id"
        if (!config.contains(path)) return

        displayName = ChatColor.translateAlternateColorCodes('&', config.getString("$path.display_name", id)!!)
        cooldownSeconds = config.getDouble("$path.cooldown", 5.0)
        manaCost = config.getDouble("$path.mana_cost", 10.0)
        minLevel = config.getInt("$path.min_level", 1)
    }

    fun tryCast(player: Player): SkillResult {
        val data = plugin.playerManager.getPlayerData(player.uniqueId) ?: return SkillResult.ERROR

        if (!data.unlockedSkills.contains(id)) return SkillResult.NOT_UNLOCKED

        if (data.currentMana < manaCost) {
            player.sendActionBar(net.kyori.adventure.text.Component.text("§cFalta Maná: ${data.currentMana.toInt()}/${manaCost.toInt()}"))
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f)
            return SkillResult.NO_MANA
        }

        val remaining = data.getCooldown(id)
        if (remaining > 0) {
            val formatted = String.format("%.1f", remaining / 1000.0)
            player.sendActionBar(net.kyori.adventure.text.Component.text("§cEn enfriamiento: ${formatted}s"))
            player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 2f)
            return SkillResult.ON_COOLDOWN
        }

        val success = onCast(player)

        if (data.currentMana < manaCost) {
            player.sendActionBar(Component.text("§cFalta Maná: ..."))
            data.consumeMana(manaCost)
            data.setCooldown(id, (cooldownSeconds * 1000).toLong())
            return SkillResult.SUCCESS
        }

        return SkillResult.ERROR
    }


    abstract fun onCast(player: Player): Boolean
}