package org.ReDiego0.orbisCore.config

data class SkillInfo(
    val id: String,
    val displayName: String,
    val minLevel: Int,
    val validSlot: SkillSlot,
    val description: List<String>,
    val manaCost: Double,
    val cooldown: Double // en segundos
)