package org.ReDiego0.orbisCore.player

import org.ReDiego0.orbisCore.config.SkillSlot
import java.util.UUID

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

    fun addExperience(amount: Double) {
        this.experience += amount
        // TODO: Lógica de subir de nivel y desbloquear skills por nivel
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