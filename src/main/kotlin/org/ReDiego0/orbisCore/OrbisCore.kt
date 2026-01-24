package org.ReDiego0.orbisCore

import org.ReDiego0.orbisCore.modules.player.PlayerListener
import org.ReDiego0.orbisCore.modules.player.PlayerManager
import org.bukkit.plugin.java.JavaPlugin

class OrbisCore : JavaPlugin() {

    lateinit var playerManager: PlayerManager
        private set

    override fun onEnable() {
        logger.info("Iniciando OrbisCore...")

        playerManager = PlayerManager(this)
        server.pluginManager.registerEvents(PlayerListener(playerManager), this)

        logger.info("OrbisCore cargado correctamente.")
    }

    override fun onDisable() {
        if (::playerManager.isInitialized) {
            playerManager.saveAll()
        }
    }
}