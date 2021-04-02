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

package com.miguel.game.rplayer

import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object RobotoPlayerManager {

    private val robotoPlayers: HashMap<UUID, RobotoPlayer> = HashMap()

    fun add(player: Player, robotoPlayer: RobotoPlayer) {
        if (!robotoPlayers.containsKey(player.uniqueId))
            robotoPlayers[player.uniqueId] = robotoPlayer
    }

    fun getrobotoPlayer(player: Player): RobotoPlayer {
        if (!robotoPlayers.containsKey(player.uniqueId))
            robotoPlayers[player.uniqueId] = RobotoPlayer(player)

        return robotoPlayers[player.uniqueId]!!
    }

    fun getAll(): MutableList<RobotoPlayer> {
        return robotoPlayers.values.toMutableList()
    }

    fun getPlaying(): MutableList<RobotoPlayer> {
        val list: MutableList<RobotoPlayer> = ArrayList()

        getAll().forEach { rplayer ->
            if (rplayer.isPlaying()) {
                list.add(rplayer)
            }
        }

        return list
    }

    fun isPlaying(player: Player): Boolean {
        return getrobotoPlayer(player).isPlaying()
    }
}