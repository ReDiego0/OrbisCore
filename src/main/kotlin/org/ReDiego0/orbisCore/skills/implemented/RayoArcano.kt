package org.ReDiego0.orbisCore.skills.implemented

import org.ReDiego0.orbisCore.OrbisCore
import org.ReDiego0.orbisCore.skills.Skill
import org.ReDiego0.orbisCore.skills.SkillType
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class RayoArcano(plugin: OrbisCore) : Skill(plugin, "rayo_arcano", SkillType.ACTIVE) {
    override fun onCast(player: Player): Boolean {
        val start = player.eyeLocation
        val direction = start.direction.normalize()
        val range = 30.0 // Rango máximo

        player.playSound(start, Sound.BLOCK_CONDUIT_ACTIVATE, 2f, 1.8f)

        var hitEntity: LivingEntity? = null
        val currentLoc = start.clone()

        var distanceTraveled = 0.0

        while (distanceTraveled < range) {
            currentLoc.add(direction.clone().multiply(0.5))
            distanceTraveled += 0.5

            val dust = Particle.DustOptions(Color.AQUA, 1.0f)
            currentLoc.world.spawnParticle(Particle.DUST, currentLoc, 1, 0.0, 0.0, 0.0, 0.0, dust)

            val targets = currentLoc.getNearbyEntities(0.5, 0.5, 0.5)
                .filterIsInstance<LivingEntity>()
                .filter { it != player }

            if (targets.isNotEmpty()) {
                hitEntity = targets.first()
                break
            }

            if (currentLoc.block.type.isSolid) {
                currentLoc.world.spawnParticle(Particle.BLOCK_CRUMBLE, currentLoc, 10, 0.5, 0.5, 0.5, currentLoc.block.blockData)
                break
            }
        }

        if (hitEntity != null) {
            hitEntity.damage(12.0, player)
            hitEntity.world.spawnParticle(Particle.FIREWORK, hitEntity.location.add(0.0, 1.0, 0.0), 20, 0.5, 0.5, 0.5, 0.1)
            hitEntity.world.playSound(hitEntity.location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 0.5f, 2f)
        }

        return true
    }
}