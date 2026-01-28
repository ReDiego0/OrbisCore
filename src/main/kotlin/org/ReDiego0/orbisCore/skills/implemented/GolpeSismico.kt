package org.ReDiego0.orbisCore.skills.implemented

import org.ReDiego0.orbisCore.OrbisCore
import org.ReDiego0.orbisCore.skills.Skill
import org.ReDiego0.orbisCore.skills.SkillType
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

// Ataque en área y efecto de lentitud a enemigos
// Habilidad de Vanguardia

class GolpeSismico(plugin: OrbisCore) : Skill(plugin, "golpe_sismico", SkillType.ACTIVE) {
    override fun onCast(player: Player): Boolean {
        val loc = player.location
        loc.world.playSound(loc, Sound.BLOCK_ANVIL_LAND, 0.5f, 0.5f)
        loc.world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.5f)

        loc.world.spawnParticle(Particle.BLOCK_CRUMBLE, loc, 50, 3.0, 0.2, 3.0, 0.0, org.bukkit.Material.COARSE_DIRT.createBlockData())
        loc.world.spawnParticle(Particle.CRIT, loc, 30, 3.0, 0.5, 3.0, 0.1)

        val radius = 5.0
        val targets = loc.getNearbyEntities(radius, 2.0, radius)
            .filterIsInstance<LivingEntity>()
            .filter { it != player }

        if (targets.isEmpty()) {
            return true
        }

        for (target in targets) {
            target.damage(10.0, player)
            target.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 80, 1))
            target.velocity = target.velocity.setY(0.4)
        }

        return true
    }
}