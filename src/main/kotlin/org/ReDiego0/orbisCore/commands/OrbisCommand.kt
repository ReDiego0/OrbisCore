package org.ReDiego0.orbisCore.commands

import org.ReDiego0.orbisCore.OrbisCore
import org.ReDiego0.orbisCore.config.SkillSlot
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class OrbisCommand(private val plugin: OrbisCore) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true
        if (!sender.hasPermission("orbis.admin")) {
            sender.sendMessage("§cSin permisos.")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("§eUso: /orbis <setclass|equip|unlock|addxp>")
            return true
        }

        val data = plugin.playerManager.getPlayerData(sender.uniqueId) ?: return true

        when (args[0].lowercase()) {
            "setclass" -> {
                // /orbis setclass VANGUARDIA
                if (args.size < 2) return false
                val classId = args[1].uppercase()

                if (plugin.classRegistry.getClass(classId) == null) {
                    sender.sendMessage("§cEsa clase no existe en classes.yml")
                    return true
                }

                data.resetClassData(classId)
                sender.sendMessage("§aClase cambiada a $classId. Skills reseteadas.")
            }

            "unlock" -> {
                // /orbis unlock golpe_sismico
                if (args.size < 2) return false
                val skillId = args[1].lowercase()
                data.unlockedSkills.add(skillId)
                sender.sendMessage("§aSkill '$skillId' desbloqueada forzosamente.")
            }

            "equip" -> {
                // /orbis equip Q golpe_sismico
                if (args.size < 3) return false
                val slotStr = args[1].uppercase()
                val skillId = args[2].lowercase()

                val slot = try { SkillSlot.valueOf(slotStr) } catch (e: Exception) {
                    sender.sendMessage("§cSlot inválido. Usa Q o F."); return true
                }

                if (data.equipSkill(slot, skillId)) {
                    sender.sendMessage("§aEquipada $skillId en $slot")
                } else {
                    sender.sendMessage("§cError: No tienes desbloqueada esa skill.")
                }
            }
        }
        return true
    }
}