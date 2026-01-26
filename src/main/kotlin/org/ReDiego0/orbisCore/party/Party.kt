package org.ReDiego0.orbisCore.party

import java.util.UUID

enum class PartyType(val maxPlayers: Int) {
    NORMAL(3),
    RAID(6)
}

data class Party(
    val id: UUID = UUID.randomUUID(),
    var leader: UUID,
    val members: MutableSet<UUID> = HashSet(),
    var type: PartyType = PartyType.NORMAL
) {
    init {
        members.add(leader)
    }

    fun isFull(): Boolean = members.size >= type.maxPlayers

    fun broadcast(message: String) {
        null
    }
}