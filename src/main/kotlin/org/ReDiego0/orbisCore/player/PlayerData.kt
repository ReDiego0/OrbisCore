package org.ReDiego0.orbisCore.player

import org.ReDiego0.orbisCore.config.SkillSlot
import org.bukkit.Bukkit
import org.bukkit.Sound
import java.util.UUID
import kotlin.math.pow

data class PlayerData(
    val uuid: UUID,
    var className: String = "NONE",
    var level: Int = 1,
    var experience: Double = 0.0,
    var currentMana: Double = 100.0,
    var maxMana: Double = 100.0,

    val unlockedSkills: MutableSet<String> = HashSet(),

    val equippedSkills: MutableMap<SkillSlot, String> = HashMap()
) {

    fun getRequiredExp(): Double {
        val baseXp = 100.0
        val multiplier = 1.2
        return baseXp * (multiplier.pow(level - 1))
    }

    fun addExperience(amount: Double) {
        this.experience += amount
        checkLevelUp()
    }

    val MAX_LEVEL_BASE = 50

    private fun checkLevelUp() {
        if (level >= MAX_LEVEL_BASE) return

        var required = getRequiredExp()

        while (this.experience >= required && level < MAX_LEVEL_BASE) {
            this.experience -= required
            this.level++

            val player = Bukkit.getPlayer(uuid)
            if (player != null) {
                player.playSound(player.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f)
                player.sendTitle("§e¡NIVEL ASCENDIDO!", "§fAhora eres Nivel §a$level", 10, 70, 20)
                player.sendMessage("§aHas alcanzado el nivel $level. ¡Revisa el menú de habilidades!")
                updateStatsOnLevelUp()
            }

            if (level == MAX_LEVEL_BASE) {
                Bukkit.getPlayer(uuid)?.sendMessage("§6§l¡HAS ALCANZADO EL NIVEL MÁXIMO DE TU CLASE! Busca al Maestro para evolucionar.")
            }
            required = getRequiredExp()
        }
    }

    private fun updateStatsOnLevelUp() {
        this.maxMana += 10
    }

    fun resetClassData(newClass: String) {
        this.className = newClass
        this.unlockedSkills.clear()
        this.equippedSkills.clear()
        this.currentMana = 0.0
    }

    fun isSkillUnlocked(skillId: String): Boolean = unlockedSkills.contains(skillId)

    fun equipSkill(slot: SkillSlot, skillId: String): Boolean {
        if (!isSkillUnlocked(skillId)) return false

        val currentSlot = equippedSkills.entries.find { it.value == skillId }?.key
        if (currentSlot != null) {
            equippedSkills.remove(currentSlot)
        }

        equippedSkills[slot] = skillId
        return true
    }
}