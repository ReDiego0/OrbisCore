package org.ReDiego0.orbisCore.utils

import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.function.Predicate

object OrbisUtils {
    fun getTargetsInRadius(center: Location, radius: Double, caster: Player): List<LivingEntity> {
        return center.getNearbyEntities(radius, radius, radius)
            .filterIsInstance<LivingEntity>()
            .filter { it != caster } // No pegarse a sí mismo
        // .filter { it !is TamedAnimal } // Futuro: no pegar mascotas
        // .filter { !PartyManager.areAllies(caster, it) } // Futuro: Party
    }

    fun drawLine(start: Location, end: Location, particle: Particle, count: Int) {
        val world = start.world
        val distance = start.distance(end)
        val vector = end.toVector().subtract(start.toVector()).normalize()

        val points = (distance * 2).toInt() // 2 partículas por bloque
        val gap = distance / points

        val current = start.clone()
        for (i in 0 until points) {
            world.spawnParticle(particle, current, 1, 0.0, 0.0, 0.0, 0.0)
            current.add(vector.clone().multiply(gap))
        }
    }
}