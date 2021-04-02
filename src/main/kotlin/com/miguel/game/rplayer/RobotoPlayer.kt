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

import com.miguel.game.kit.Kit
import com.miguel.game.manager.GameManager
import com.miguel.listener.habilities.None
import com.miguel.scoreboard.ScoreboardManager
import com.miguel.util.Permissions
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class RobotoPlayer(val player: Player) {

    private var spectating = false
    private var vanished = false
    private var admin = false

    var primaryKit: Kit = None()
    var secondaryKit: Kit = None()

    var scoreboardManager: ScoreboardManager? = null

    fun spectate() {
        if (!player.hasPermission(Permissions.SPECTATE)) {
            player.kickPlayer("Você morreu! boa sorte na próxima")
            return
        }

        vanish()

        player.health = player.maxHealth

        player.gameMode = GameMode.SPECTATOR

        player.allowFlight = true

        player.velocity = Vector(.0, 1.0, .0)

        player.isFlying = true

        spectating = true
    }

    private fun vanish() {
        vanished = !vanished

        GameManager.players.forEach { pl ->
            if (vanished) {
                pl.hidePlayer(player)
            } else {
                pl.showPlayer(player)
            }
        }
    }

    fun admin() {
        if (!player.hasPermission(Permissions.ADMIN))
            return

        vanish()

        admin = !admin

        player.inventory.clear()
        player.inventory.armorContents = null

        player.gameMode = if (admin) GameMode.CREATIVE else GameMode.SURVIVAL
    }

    fun isPlaying(): Boolean {
        return !spectating && !vanished && !admin && player.isOnline
    }

    fun scoreboard(state: Boolean) {
        if (scoreboardManager != null) {
            if (state) {
                if (scoreboardManager!!.isActivated())
                    scoreboardManager!!.destroy()

                scoreboardManager!!.build()
            } else {
                if (scoreboardManager!!.isActivated())
                    scoreboardManager!!.destroy()
            }
        }
    }

    fun scoreboard() {
        if (scoreboardManager != null) {
            if (scoreboardManager!!.isActivated()) {
                scoreboardManager!!.destroy()
            } else {
                scoreboardManager!!.build()
            }
        }
    }
}