package org.ReDiego0.orbisCore

import org.ReDiego0.orbisCore.config.ClassRegistry
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
            logger.info("Hook con PlaceholderAPI registrado correctamente.")
        } else {
            logger.warning("PlaceholderAPI no encontrado. Los menús no funcionarán bien.")
        }

        logger.info("OrbisCore cargado correctamente.")
    }

    override fun onDisable() {
        if (::playerManager.isInitialized) {
            playerManager.saveAll()
        }
    }
}