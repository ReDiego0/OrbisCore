package org.ReDiego0.orbisCore.skills

import org.ReDiego0.orbisCore.OrbisCore
import org.bukkit.entity.Player

class SkillManager(private val plugin: OrbisCore) {

    private val skills = HashMap<String, Skill>()

    fun registerSkill(skill: Skill) {
        skills[skill.id] = skill
    }

    fun getSkill(id: String): Skill? {
        return skills[id.lowercase()]
    }

    fun reloadSkillsConfig() {
        val config = plugin.classRegistry.classesConfig ?: return
        val section = config.getConfigurationSection("classes") ?: return
        section.getKeys(false).forEach { classId ->
            for (skill in skills.values) {
                val path = "classes.$classId.available_skills.${skill.id}"
                if (config.contains(path)) {
                    skill.loadConfig(config, classId)
                }
            }
        }
        plugin.logger.info("Recargadas configs de ${skills.size} habilidades.")
    }

    fun castSlot(player: Player, slot: org.ReDiego0.orbisCore.config.SkillSlot) {
        val data = plugin.playerManager.getPlayerData(player.uniqueId) ?: return
        val skillId = data.equippedSkills[slot]

        if (skillId == null) {
            player.sendMessage("§cNo tienes ninguna habilidad equipada en $slot.")
            return
        }

        val skill = getSkill(skillId)
        if (skill != null) {
            skill.tryCast(player)
        } else {
            player.sendMessage("§cError: La habilidad equipada '$skillId' no existe.")
        }
    }
}