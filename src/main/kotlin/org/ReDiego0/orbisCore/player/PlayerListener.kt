package org.ReDiego0.orbisCore.modules.player

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerListener(private val manager: PlayerManager) : Listener {

    @EventHandler
    fun onAsyncLogin(event: AsyncPlayerPreLoginEvent) {
        try {
            manager.loadPlayer(event.uniqueId)
        } catch (e: Exception) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Error cargando tu perfil. Contacta a un admin.")
            e.printStackTrace()
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        // TODO: Usar un scheduler
        manager.saveAndRemovePlayer(event.player.uniqueId)
    }
}