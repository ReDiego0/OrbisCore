package org.ReDiego0.orbisCore.commands

import org.ReDiego0.orbisCore.OrbisCore
import org.ReDiego0.orbisCore.config.SkillSlot
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class OrbisCommand(private val plugin: OrbisCore) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) return false

        when (args[0].lowercase()) {

            "setclass" -> {
                if (!sender.hasPermission("orbis.admin")) return true
                if (args.size < 3) {
                    sender.sendMessage("§cUso: /orbis setclass <player> <clase>")
                    return true
                }
                val target = Bukkit.getPlayer(args[1]) ?: return true
                val classId = args[2].uppercase()

                if (plugin.classRegistry.getClass(classId) == null) {
                    sender.sendMessage("§cLa clase $classId no existe.")
                    return true
                }

                val data = plugin.playerManager.getPlayerData(target.uniqueId) ?: return true

                data.className = classId
                data.unlockedSkills.clear()
                data.equippedSkills.clear()
                data.currentMana = data.maxMana

                target.sendMessage("§e¡Tu clase ha cambiado a §6$classId§e!")
                target.sendTitle("§6CLASE CAMBIADA", "§fAhora eres un §e$classId", 10, 70, 20)
                sender.sendMessage("§aClase cambiada correctamente.")
            }

            "unlock" -> {
                if (!sender.hasPermission("orbis.admin")) return true
                if (args.size < 3) return true

                val target = Bukkit.getPlayer(args[1]) ?: return true
                val skillId = args[2].lowercase()

                val data = plugin.playerManager.getPlayerData(target.uniqueId) ?: return true

                if (data.unlockedSkills.contains(skillId)) {
                    sender.sendMessage("§cEl jugador ya tiene esa habilidad.")
                    return true
                }

                data.unlockedSkills.add(skillId)
                target.sendMessage("§a¡Has aprendido una nueva habilidad!")
                sender.sendMessage("§aHabilidad $skillId desbloqueada para ${target.name}.")
            }

            "equip" -> {
                if (!sender.hasPermission("orbis.admin")) return true
                if (args.size < 4) return true

                val target = Bukkit.getPlayer(args[1]) ?: return true
                val slotStr = args[2].uppercase() // Q o F
                val skillId = args[3].lowercase()

                val data = plugin.playerManager.getPlayerData(target.uniqueId) ?: return true

                if (!data.unlockedSkills.contains(skillId)) {
                    sender.sendMessage("§cEl jugador no ha desbloqueado esa habilidad.")
                    return true
                }

                val slot = try { SkillSlot.valueOf(slotStr) } catch (e: Exception) {
                    sender.sendMessage("§cSlot inválido. Usa Q o F.")
                    return true
                }

                val classInfo = plugin.classRegistry.getClass(data.className) ?: return true
                val skillInfo = classInfo.skills[skillId]

                if (skillInfo == null) {
                    sender.sendMessage("§cEsa skill no es de tu clase.")
                    return true
                }

                if (skillInfo.validSlot != slot) {
                    target.sendMessage("§cEsta habilidad solo se puede equipar en la ranura ${skillInfo.validSlot}.")
                    return true
                }

                data.equippedSkills[slot] = skillId
                target.sendMessage("§aHabilidad equipada en §e$slot§a.")
            }

            "unequip" -> {
                if (!sender.hasPermission("orbis.admin")) return true
                val target = Bukkit.getPlayer(args[1]) ?: return true
                val slotStr = args[2].uppercase()
                val slot = try { SkillSlot.valueOf(slotStr) } catch (e: Exception) { return true }

                val data = plugin.playerManager.getPlayerData(target.uniqueId) ?: return true
                data.equippedSkills.remove(slot)
                target.sendMessage("§7Ranura $slot vaciada.")
            }
        }
        return true
    }
}