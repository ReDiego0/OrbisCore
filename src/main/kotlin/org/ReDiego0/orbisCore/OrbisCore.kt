package org.ReDiego0.orbisCore

import org.ReDiego0.orbisCore.config.ClassRegistry
import org.ReDiego0.orbisCore.ether.EtherTask // <--- Import nuevo
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

    override fun onEnable() {
        logger.info("Iniciando OrbisCore...")

        classRegistry = ClassRegistry(this)
        classRegistry.loadClasses()

        playerManager = PlayerManager(this)
        server.pluginManager.registerEvents(PlayerListener(playerManager), this)

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            OrbisPapiExpansion(this).register()
        }

        EtherTask(this).runTaskTimer(this, 0L, 20L)

        logger.info("OrbisCore cargado correctamente.")
    }

    override fun onDisable() {
        Bukkit.getScheduler().cancelTasks(this)

        if (::playerManager.isInitialized) {
            playerManager.saveAll()
        }
    }
}