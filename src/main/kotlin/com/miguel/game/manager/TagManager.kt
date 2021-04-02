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

package com.miguel.game.manager

import com.miguel.RobotoHG
import com.miguel.common.TagCommon
import org.bukkit.scheduler.BukkitRunnable

class TagManager {

    init {
        object : BukkitRunnable() {
            override fun run() {
                GameManager.players.forEach { player ->
                    val scoreboard = player.scoreboard

                    val tag = TagCommon.getTag(player)

                    val priority = tag.tagPriority.toString()

                    if (scoreboard.getTeam(priority) == null)
                        scoreboard.registerNewTeam(priority)

                    if (!scoreboard.getTeam(priority).hasPlayer(player))
                        scoreboard.getTeam(priority).addPlayer(player)

                    val formattedTag = "${tag.formattedNametag}§7"

                    if (scoreboard.getTeam(priority).prefix != formattedTag)
                        scoreboard.getTeam(priority).prefix = formattedTag
                }
            }
        }.runTaskTimerAsynchronously(RobotoHG.INSTANCE, 0L, 20L)
    }
}