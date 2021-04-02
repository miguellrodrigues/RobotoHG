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

import com.miguel.RobotoHG
import com.miguel.common.TagCommon
import com.miguel.game.GameStage
import com.miguel.game.Timer
import com.miguel.game.data.PlayerData
import com.miguel.game.kit.KitManager
import com.miguel.game.manager.FeastManager
import com.miguel.game.manager.GameManager
import com.miguel.game.manager.Manager
import com.miguel.game.manager.MiniFeastManager
import com.miguel.game.rplayer.RobotoPlayerManager
import com.miguel.util.Permissions
import com.miguel.util.Strings
import com.miguel.util.Structures
import com.miguel.util.Values
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.server.MapInitializeEvent
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import org.bukkit.map.MinecraftFont
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import javax.imageio.ImageIO

class GameEvents : Listener {

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (Timer.gameStage == GameStage.PREGAME || Structures.feast.locations.contains(event.block)) {
            event.isCancelled = true
        } else {
            val block = event.block

            if (!block.type.name.contains("ORE")) {
                block.drops.forEach { itemStack ->
                    event.player.inventory.addItem(itemStack)
                }

                block.type = Material.AIR
            }
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        event.isCancelled = Timer.gameStage == GameStage.PREGAME

        if (Structures.feast.locations.contains(event.blockAgainst)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val player = event.player
        val robotoPlayer = RobotoPlayerManager.getrobotoPlayer(player)

        if (robotoPlayer.primaryKit.getItems().contains(event.itemDrop.itemStack)
                || robotoPlayer.secondaryKit.getItems().contains(event.itemDrop.itemStack)
        ) {
            event.isCancelled = true

            event.player.updateInventory()
        }

        if (Timer.gameStage == GameStage.PREGAME) {
            event.isCancelled = true
            event.player.updateInventory()
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val inventory = event.inventory

        if (inventory.type == InventoryType.ENCHANTING) {
            inventory.clear()
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        if (player.itemInHand.type == Material.MUSHROOM_SOUP && event.action.name.contains("RIGHT")) {

            val health = player.health
            val maxHealth = player.maxHealth

            if (health != maxHealth) {
                player.health = if ((health + 5.5) >= maxHealth) maxHealth else health + 5.5
                player.itemInHand.type = Material.BOWL
            }
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (Timer.gameStage == GameStage.GAME) {
            val player = event.entity

            if (player is Player) {
                if (player.health - event.finalDamage <= 0) {
                    event.isCancelled = true

                    player.lastDamageCause = event

                    val victimData = PlayerData.getData(player.uniqueId)!!

                    val killer = player.killer

                    if (killer != null) {
                        val killerData = PlayerData.getData(killer.uniqueId)!!
                        killerData.kills += 1

                        if (!Values.firstBlood) {
                            Values.firstBlood = true

                            GameManager.sendMessage("${Strings.PREFIX} §fO jogador §a${killer.name} §fFoi o §cFirstBlood §f!")

                            GameManager.playSound(Sound.AMBIENCE_THUNDER)

                            TagCommon.setTag(killer, TagCommon.FIRSTBLOOD)
                        }
                    }

                    victimData.deaths += 1

                    val drops = player.inventory.contents.toMutableList()

                    KitManager.getKits(player).forEach { kit ->
                        kit.getItems().forEach { item ->
                            if (drops.contains(item)) {
                                drops.remove(item)
                            }
                        }
                    }

                    drops.filterNotNull().forEach { item -> player.world.dropItem(player.location, item) }

                    player.inventory.clear()
                    player.inventory.armorContents = emptyArray()

                    GameManager.sendMessage(GameManager.getDeathCause(player))

                    if (player.hasPermission(Permissions.RESPAWN)) {
                        if (Timer.gameTime <= 300) {
                            player.health = player.maxHealth

                            player.teleport(Manager.getRandomLocation().add(.0, 1.0, .0))
                            player.velocity = Vector(.0, .5, .0)

                            KitManager.giveItems(player)
                        }
                    } else if (player.hasPermission(Permissions.ADMIN)) {
                        RobotoPlayerManager.getrobotoPlayer(player).admin()
                    } else if (player.hasPermission(Permissions.SPECTATE)) {
                        RobotoPlayerManager.getrobotoPlayer(player).spectate()
                    } else {
                        /*Manager.sendToServer(player, "lobby")*/

                        player.kickPlayer("${Strings.SERVER_NAME} \n §fVocê morreu §e! \n §fBoa sorte na próxima")
                    }
                }
            }
        }
    }

    @EventHandler
    fun onMapInitialize(event: MapInitializeEvent) {
        if (Timer.gameStage == GameStage.WIN) {
            val map = event.map
            val serverName = ChatColor.stripColor(Strings.SERVER_NAME)

            map.renderers.forEach { renderer -> event.map.removeRenderer(renderer) }

            map.addRenderer(object : MapRenderer() {
                override fun render(view: MapView, canvas: MapCanvas, player: Player) {
                    val icon = ImageIO.read(RobotoHG.INSTANCE.getResource("cake.png"))

                    canvas.drawText(48, 10, MinecraftFont.Font, serverName)
                    canvas.drawText(32 - ("Voce ganhou !".length / 2) + 8, 20, MinecraftFont.Font, "Voce ganhou !")
                    canvas.drawText(32 - (player.name.length / 2), 30, MinecraftFont.Font, player.name)

                    canvas.drawImage(
                            15,
                            42,
                            icon
                    )
                }
            })
        }
    }

    @EventHandler
    fun onPlayerHeldItem(event: PlayerItemHeldEvent) {
        val player = event.player

        val item = player.inventory.getItem(event.newSlot)
        if (item != null) {
            val name = item.type.name

            if (name.contains("COMPASS")) {
                object : BukkitRunnable() {
                    var target: Player? = null

                    override fun run() {
                        if (player.itemInHand.type != Material.COMPASS) {
                            cancel()
                            return
                        }

                        player.world.players.forEach { nearbyPlayer ->
                            if (nearbyPlayer.location.distance(player.location) >= 25) {
                                target = nearbyPlayer
                                return@forEach
                            }
                        }

                        if (target == null) {
                            when (player.compassTarget) {
                                FeastManager.location -> {
                                    GameManager.sendActionBar(
                                            player, "Bússola apontando para o §aFeast"
                                    )
                                }

                                MiniFeastManager.location -> {
                                    GameManager.sendActionBar(
                                            player, "Bússola apontando para o último §aMiniFeast"
                                    )
                                }

                                else -> {
                                    GameManager.sendActionBar(player, "§vNenhum jogador encontrado")
                                }
                            }
                        } else {
                            GameManager.sendActionBar(
                                    player,
                                    "§fNome §e${target!!.name}" + " §fDistancia: §e${
                                        player.location.distance(target!!.location)
                                                .toInt()
                                    }" + " §f▲ Y: §e${(target!!.location.y - player.location.y).toInt()}"
                            )

                            player.compassTarget = target!!.location
                        }
                    }
                }.runTaskTimerAsynchronously(RobotoHG.INSTANCE, 0L, 10L)
            }
        }
    }

    @EventHandler
    fun onAsyncPlayerChat(event: AsyncPlayerChatEvent) {
        event.isCancelled = !Values.GLOBAL_CHAT
    }
}