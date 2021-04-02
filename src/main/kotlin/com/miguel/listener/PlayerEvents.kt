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

import com.miguel.common.TagCommon
import com.miguel.game.GameStage
import com.miguel.game.Timer
import com.miguel.game.data.PlayerData
import com.miguel.game.manager.GameManager
import com.miguel.manager.PlayerManager
import com.miguel.packet.bossbar.BossBarManager
import com.miguel.packet.hologram.HologramManager
import com.miguel.packet.npc.NpcManager
import com.miguel.util.Permissions
import com.miguel.util.Strings
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.*

class PlayerEvents : Listener {

    @EventHandler
    fun onPlayerLogin(event: PlayerLoginEvent) {
        val player = event.player

        if (Timer.gameStage != GameStage.PREGAME) {
            if (!PlayerManager.hasPermission(player.uniqueId, Permissions.JOIN)) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cA partida já iniciou!")
            } else {
                PlayerData.createData(player.uniqueId)
            }
        } else {
            PlayerData.createData(player.uniqueId)
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        event.joinMessage = null

        GameManager.init(player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player

        player.inventory.clear()
        player.inventory.armorContents = emptyArray()

        NpcManager.removeNpc(player)
        HologramManager.getHologram(player)?.destroy()

        if (Timer.gameStage == GameStage.GAME) {
            event.quitMessage = "${Strings.PREFIX} §fO jogador §a${player.name} §fSe desconectou em meio a partida §e!"
        } else {
            event.quitMessage = null
        }

        TagCommon.removeTag(player)
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player

        if (Timer.gameStage == GameStage.PREGAME) {
            val npc = NpcManager.getNpc(player)
            npc?.teleport(npc.location.setDirection(player.location.subtract(npc.location).toVector()))
        }
    }

    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        if (Timer.gameStage == GameStage.GAME) {
            event.foodLevel = 10 + (event.foodLevel * 0.5).toInt()
        } else {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val player = event.player

        BossBarManager.getBossBar(player)?.update()
    }

    /*@EventHandler
    fun onPlayerCommandPreProcess(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        val message: String = event.message.split(" ")[0]

        val helpTopic = Bukkit.getServer().helpMap.getHelpTopic(message)

        if (helpTopic != null) {
            if (!message.startsWith("/hg")) {
                //event.isCancelled = true
            }
        }
    }*/

    @EventHandler
    fun onPlayerAsyncChat(event: AsyncPlayerChatEvent) {
        val player = event.player

        event.format =
                TagCommon.getTag(player).formattedNametag +
                        "§7" + player.name + " §b§l→ §f" + event.message
    }
}