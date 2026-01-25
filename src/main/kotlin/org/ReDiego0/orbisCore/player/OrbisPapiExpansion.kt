package org.ReDiego0.orbisCore.player

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.ReDiego0.orbisCore.OrbisCore
import org.ReDiego0.orbisCore.config.SkillSlot
import org.bukkit.entity.Player

class OrbisPapiExpansion(private val plugin: OrbisCore) : PlaceholderExpansion() {

    override fun getIdentifier(): String = "orbis"
    override fun getAuthor(): String = "ReDiego0"
    override fun getVersion(): String = plugin.description.version
    override fun persist(): Boolean = true

    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        if (player == null) return ""

        val data = plugin.playerManager.getPlayerData(player.uniqueId) ?: return ""
        val classInfo = plugin.classRegistry.getClass(data.className)

        return when {
            params == "class_name" -> classInfo?.displayName ?: "Sin Clase"
            params == "class_id" -> data.className
            params == "level" -> data.level.toString()
            params == "exp" -> data.experience.toString()

            params == "mana_current" -> String.format("%.1f", data.currentMana)
            params == "mana_max" -> String.format("%.1f", data.maxMana)
            params == "mana_int" -> data.currentMana.toInt().toString()

            params == "exp_current" -> data.experience.toInt().toString()
            params == "exp_required" -> data.getRequiredExp().toInt().toString()
            params == "exp_percentage" -> {
                val percent = (data.experience / data.getRequiredExp()) * 100
                String.format("%.1f", percent) // "50.5"
            }

            params == "exp_bar" -> getProgressBar(data.experience, data.getRequiredExp())

            params.startsWith("skill_unlocked_") -> {
                val skillId = params.removePrefix("skill_unlocked_")
                data.isSkillUnlocked(skillId).toString()
            }

            params.startsWith("skill_equipped_") -> {
                val slotStr = params.removePrefix("skill_equipped_")
                val slot = try { SkillSlot.valueOf(slotStr) } catch (e: Exception) { return "INVALID_SLOT" }
                val skillId = data.equippedSkills[slot] ?: return "Ninguna"

                val skillInfo = classInfo?.skills?.get(skillId)
                skillInfo?.displayName ?: skillId
            }

            else -> null
        }
    }

    private fun getProgressBar(current: Double, max: Double): String {
        val totalBars = 10
        val percent = current / max
        val progressBars = (totalBars * percent).toInt()
        val sb = StringBuilder("&a")

        for (i in 0 until totalBars) {
            if (i == progressBars) sb.append("&7") // Cambia a color gris para lo vacío
            sb.append("|")
        }
        return sb.toString()
    }
}