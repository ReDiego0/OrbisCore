package org.ReDiego0.orbisCore.instances

import org.ReDiego0.orbisCore.OrbisCore
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class InstanceCommand(private val plugin: OrbisCore) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) return false
        when (args[0].lowercase()) {
            "start" -> {
                if (sender !is Player) return true
                if (args.size < 2) {
                    sender.sendMessage("§cUso: /orbisinstance start <template_name>")
                    return true
                }
                val template = args[1]

               val party = plugin.partyManager.getParty(sender.uniqueId)
//                if (party == null) {
//                    sender.sendMessage("§cDebes estar en una Party para entrar.")
//                    return true
//                }

                if (party?.leader != sender.uniqueId) {
                    sender.sendMessage("§cSolo el líder de la party puede iniciar la instancia.")
                    return true
                }

                sender.sendMessage("§ePreparando instancia... (Teletransporte en 3s)")
                plugin.instanceManager.startInstance(party.id, template)
            }

            "end" -> {
                if (!sender.hasPermission("orbis.admin")) return true

                if (args.size < 2) return false
                val worldName = args[1]

                plugin.instanceManager.endInstance(worldName)
                sender.sendMessage("§eInstancia $worldName finalizada.")
            }
        }
        return true
    }
}