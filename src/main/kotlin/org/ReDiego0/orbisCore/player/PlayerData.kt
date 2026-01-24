package org.ReDiego0.orbisCore.modules.player

import java.util.UUID

data class PlayerData(
    val uuid: UUID,
    var className: String = "NONE",
    var level: Int = 1,
    var experience: Double = 0.0,
    var currentMana: Double = 100.0,
    var maxMana: Double = 100.0
) {

    fun addExperience(amount: Double) {
        this.experience += amount
        // TODO: Lógica de subir de nivel
    }
}