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

package com.miguel.packet.bossbar

import com.miguel.game.manager.GameManager
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

object BossBarManager {

    private val barMap: HashMap<UUID, BossBar> = HashMap()

    fun sendBossBar(message: String) {
        GameManager.players.forEach { player ->
            if (!barMap.containsKey(player.uniqueId)) {
                val bossBar = BossBar(player, message)
                bossBar.spawn()
                barMap[player.uniqueId] = bossBar
            }
        }
    }

    fun updateAll() {
        barMap.values.forEach { bar ->
            bar.update()
        }
    }

    fun destroyAll() {
        barMap.values.forEach { bar ->
            bar.destroy()
        }

        barMap.clear()
    }

    fun changeLife(health: Float) {
        barMap.values.forEach { bar ->
            bar.changeHealth(health)
        }
    }

    fun getBossBar(player: Player): BossBar? {
        if (barMap.containsKey(player.uniqueId)) {
            return barMap[player.uniqueId]!!
        }

        return null
    }
}