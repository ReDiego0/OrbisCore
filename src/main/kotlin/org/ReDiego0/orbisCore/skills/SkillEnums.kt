package org.ReDiego0.orbisCore.skills

enum class SkillType {
    ACTIVE,    // Se activa con Q
    TACTICAL,  // Se activa con F
    PASSIVE
}

enum class SkillResult {
    SUCCESS,
    NOT_UNLOCKED,
    NO_MANA,
    ON_COOLDOWN,
    WRONG_ITEM, // requiere arma específica
    ERROR
}