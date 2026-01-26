package org.ReDiego0.orbisCore

import org.ReDiego0.orbisCore.combat.CombatListener
import org.ReDiego0.orbisCore.combat.SkillExecutor
import org.ReDiego0.orbisCore.commands.OrbisCommand
import org.ReDiego0.orbisCore.config.ClassRegistry
import org.ReDiego0.orbisCore.ether.EtherTask
import org.ReDiego0.orbisCore.instances.InstanceRegistry
import org.ReDiego0.orbisCore.items.ItemProvider
import org.ReDiego0.orbisCore.player.MobLootListener
import org.ReDiego0.orbisCore.player.OrbisPapiExpansion
import org.ReDiego0.orbisCore.player.PlayerListener
import org.ReDiego0.orbisCore.player.PlayerManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class OrbisCore : JavaPlugin() {

    lateinit var playerManager: PlayerManager
        private set
    lateinit var classRegistry: ClassRegistry
        private set
    lateinit var skillExecutor: SkillExecutor
    lateinit var itemProvider: ItemProvider
    lateinit var instanceRegistry: InstanceRegistry

    override fun onEnable() {
        logger.info("Iniciando OrbisCore...")

        classRegistry = ClassRegistry(this)
        classRegistry.loadClasses()

        itemProvider = ItemProvider(this)

        instanceRegistry = InstanceRegistry(this)
        instanceRegistry.loadInstances()

        playerManager = PlayerManager(this)
        server.pluginManager.registerEvents(PlayerListener(playerManager), this)

        skillExecutor = SkillExecutor(this)
        server.pluginManager.registerEvents(CombatListener(this, skillExecutor), this)
        server.pluginManager.registerEvents(MobLootListener(this), this)

        getCommand("orbis")?.setExecutor(OrbisCommand(this))

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            OrbisPapiExpansion(this).register()
        }

        EtherTask(this).runTaskTimer(this, 0L, 20L)

        logger.info("&eOrbisCore cargado correctamente.")
    }

    override fun onDisable() {
        Bukkit.getScheduler().cancelTasks(this)

        if (::playerManager.isInitialized) {
            playerManager.saveAll()
        }
    }
}