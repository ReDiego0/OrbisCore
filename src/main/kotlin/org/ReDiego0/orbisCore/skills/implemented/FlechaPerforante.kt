package org.ReDiego0.orbisCore.skills.implemented

import org.ReDiego0.orbisCore.OrbisCore
import org.ReDiego0.orbisCore.skills.Skill
import org.ReDiego0.orbisCore.skills.SkillType
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue

class FlechaPerforante(plugin: OrbisCore) : Skill(plugin, "flecha_perforante", SkillType.ACTIVE) {

    override fun onCast(player: Player): Boolean {
        val arrow = player.launchProjectile(Arrow::class.java)

        arrow.velocity = arrow.velocity.multiply(2.5)
        arrow.isCritical = true
//      arrow.knocksback = true
        arrow.setGravity(true)

        // "ORBIS_SKILL" -> "flecha_perforante"
        arrow.setMetadata("ORBIS_SKILL", FixedMetadataValue(plugin, id))

        player.world.playSound(player.location, Sound.ENTITY_ARROW_SHOOT, 1f, 0.5f)
        player.world.spawnParticle(Particle.CRIT, player.eyeLocation.add(player.eyeLocation.direction), 10, 0.1, 0.1, 0.1, 0.1)

        return true
    }
}