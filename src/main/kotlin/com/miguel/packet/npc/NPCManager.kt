/*
 *
 *  * Copyright (©) RobotoHG-1.8
 *  *
 *  * Projeto desenvolvido por Miguell Rodrigues
 *  * Todos os direitos reservados
 *  *
 *  * Modificado em: 23/05/2020 18:17
 *
 */

package com.miguel.packet.npc

import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object NpcManager {

    private val npcList: MutableList<NPC> = ArrayList()
    private val npcMap: HashMap<UUID, NPC> = HashMap()

    fun destroyAll() {
        if (npcList.isNotEmpty()) {
            npcList.forEach { npc ->
                npc.destroy()
            }
        }

        npcList.clear()
        npcMap.clear()
    }

    fun getNpc(player: Player): NPC? {
        if (npcMap.containsKey(player.uniqueId))
            return npcMap[player.uniqueId]

        return null
    }

    fun spawnNpc(player: Player?, skin: String, name: String, tab: Boolean, location: Location): NPC {
        val npc = NPC(
                skin, name, location, player
        )

        if (player != null) {
            if (!npcMap.containsKey(player.uniqueId)) {
                npcMap[player.uniqueId] = npc
            }
        }

        npc.spawn(tab)

        npcList.add(npc)

        return npc
    }

    fun removeNpc(player: Player) {
        if (!npcMap.containsKey(player.uniqueId))
            return

        val npc = npcMap[player.uniqueId]!!
        npc.destroy()

        npcList.remove(npc)
        npcMap.remove(player.uniqueId)
    }
}