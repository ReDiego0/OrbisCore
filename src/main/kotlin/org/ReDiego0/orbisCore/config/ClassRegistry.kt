package org.ReDiego0.orbisCore.config

import org.ReDiego0.orbisCore.OrbisCore
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.logging.Level

class ClassRegistry(private val plugin: OrbisCore) {
    private val loadedClasses = HashMap<String, ClassInfo>()

    fun loadClasses() {
        val file = File(plugin.dataFolder, "classes.yml")

        if (!file.exists()) {
            plugin.saveResource("classes.yml", false)
        }

        val config = YamlConfiguration.loadConfiguration(file)
        loadedClasses.clear()

        val section = config.getConfigurationSection("classes")
        if (section == null) {
            plugin.logger.warning("No se encontró la sección 'classes' en classes.yml")
            return
        }

        for (key in section.getKeys(false)) {
            try {
                val path = "classes.$key"
                val skillsMap = HashMap<String, SkillInfo>()
                val skillsSection = config.getConfigurationSection("$path.available_skills")

                if (skillsSection != null) {
                    for (skillId in skillsSection.getKeys(false)) {
                        val sPath = "$path.available_skills.$skillId"

                        val slotStr = config.getString("$sPath.slot", "PASSIVE")!!.uppercase()
                        val slotEnum = try { SkillSlot.valueOf(slotStr) } catch (e: Exception) { SkillSlot.PASSIVE }

                        val skill = SkillInfo(
                            id = skillId,
                            displayName = config.getString("$sPath.display_name", skillId)!!.replace("&", "§"),
                            mmSkill = config.getString("$sPath.mm_skill", "SkillError")!!,
                            minLevel = config.getInt("$sPath.min_level", 1),
                            validSlot = slotEnum,
                            description = config.getStringList("$sPath.description").map { it.replace("&", "§") }
                        )
                        skillsMap[skillId] = skill
                    }
                }

                val info = ClassInfo(
                    id = key.uppercase(),
                    displayName = config.getString("$path.display_name", key)!!.replace("&", "§"),
                    baseHealth = config.getDouble("$path.base_health", 20.0),
                    baseMana = config.getDouble("$path.base_mana", 100.0),
                    manaRegen = config.getDouble("$path.mana_regen", 1.0),
                    skills = skillsMap
                )
                loadedClasses[key.uppercase()] = info
            } catch (e: Exception) {
                plugin.logger.log(Level.SEVERE, "Error cargando la clase $key", e)
            }
        }

        plugin.logger.info("Cargadas ${loadedClasses.size} Vías (Clases) correctamente.")
    }

    fun getClass(id: String): ClassInfo? {
        return loadedClasses[id.uppercase()]
    }

    fun getAllClassIds(): Set<String> {
        return loadedClasses.keys
    }
}