package org.ReDiego0.orbisCore.config

data class ClassInfo(
    val id: String,
    val displayName: String,
    val baseHealth: Double,
    val baseMana: Double,
    val manaRegen: Double,
    val skills: Map<String, SkillInfo>
)