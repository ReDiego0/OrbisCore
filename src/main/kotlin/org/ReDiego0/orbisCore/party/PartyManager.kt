package org.ReDiego0.orbisCore.party

import org.ReDiego0.orbisCore.OrbisCore
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class PartyManager(private val plugin: OrbisCore) {

    private val parties = ConcurrentHashMap<UUID, Party>()
    private val playerPartyMap = ConcurrentHashMap<UUID, UUID>()
    private val invites = ConcurrentHashMap<UUID, UUID>()

    fun createParty(leader: Player, type: PartyType = PartyType.NORMAL): Party? {
        if (playerPartyMap.containsKey(leader.uniqueId)) return null

        val party = Party(leader = leader.uniqueId, type = type)
        parties[party.id] = party
        playerPartyMap[leader.uniqueId] = party.id
        return party
    }

    fun getParty(playerUuid: UUID): Party? {
        val partyId = playerPartyMap[playerUuid] ?: return null
        return parties[partyId]
    }

    fun getPartyMembers(partyId: UUID): List<UUID> {
        return parties[partyId]?.members?.toList() ?: emptyList()
    }

    fun disbandParty(partyId: UUID) {
        val party = parties.remove(partyId) ?: return
        party.members.forEach { playerPartyMap.remove(it) }
        // TODO: Avisar a los miembros
    }

    // TODO: métodos invitePlayer, acceptInvite, kickPlayer, leaveParty
}