package org.ReDiego0.orbisCore.combat

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class CooldownManager {

    private val cooldowns = ConcurrentHashMap<UUID, MutableMap<String, Long>>()

    fun isOnCooldown(uuid: UUID, skillId: String): Boolean {
        val playerCooldowns = cooldowns[uuid] ?: return false
        val cooldownEnd = playerCooldowns[skillId] ?: return false
        return System.currentTimeMillis() < cooldownEnd
    }

    fun getRemainingSeconds(uuid: UUID, skillId: String): Double {
        val playerCooldowns = cooldowns[uuid] ?: return 0.0
        val cooldownEnd = playerCooldowns[skillId] ?: return 0.0
        val diff = cooldownEnd - System.currentTimeMillis()
        return if (diff > 0) diff / 1000.0 else 0.0
    }

    fun setCooldown(uuid: UUID, skillId: String, seconds: Double) {
        val playerCooldowns = cooldowns.computeIfAbsent(uuid) { ConcurrentHashMap() }
        playerCooldowns[skillId] = System.currentTimeMillis() + (seconds * 1000).toLong()
    }
}