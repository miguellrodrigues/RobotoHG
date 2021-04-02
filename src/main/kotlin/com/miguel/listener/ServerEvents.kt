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

package com.miguel.listener

import com.miguel.game.GameStage
import com.miguel.game.Timer
import com.miguel.game.manager.Manager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent

class ServerEvents : Listener {

    @EventHandler
    fun onServerListPing(event: ServerListPingEvent) {
        when (Timer.gameStage) {
            GameStage.INVINCIBLE -> {
                event.motd = "§7[§6!§7]  §6§lHardcore§f§lGames §7Partida iniciou!" +
                        "\n§          §7Invencibilidade acaba em §f→ §a${Manager.formatTime(Timer.gameTime)}"
            }

            GameStage.GAME -> {
                event.motd = "§7[§c!§7]  §6§lHardcore§f§lGames" +
                        "\n          §7Tempo de jogo §f→ §a${Manager.formatTime(Timer.gameTime)}"
            }

            GameStage.PREGAME -> {
                event.motd = "§7[§a!§7]  §6§lHardcore§f§lGames §7Venha jogar!" +
                        "\n          §7Inicia em §f→ §a${Manager.formatTime(Timer.gameTime)}"
            }

            else -> event.motd = "§cINICIANDO...."
        }
    }
}