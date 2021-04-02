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

package com.miguel.listener.habilities

import com.miguel.game.kit.Kit
import com.miguel.game.manager.GameManager
import com.miguel.util.Strings
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

class CheckPoint : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        return arrayListOf(
                GameManager.createItem("§f§lKit §e§lCheckPoint", arrayOf(""), Material.NETHER_FENCE)
        )
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eCheckPoint",
                arrayOf(" ", "§fTeleporte-se a um local marcado", " ", "§eClique para selecionar"),
                Material.NETHER_FENCE
        )
    }

    override fun getPrice(): Int {
        return 0
    }

    override val cooldownMap: HashMap<UUID, Long> = HashMap()

    private val locationMap: HashMap<UUID, Location> = HashMap()

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player

        if (using(player)) {
            if (player.itemInHand.type == Material.NETHER_FENCE) {
                event.isCancelled = true

                if (canUse) {
                    if (locationMap.containsKey(player.uniqueId)) {
                        player.sendBlockChange(locationMap[player.uniqueId], Material.AIR, 0)
                    }

                    player.sendBlockChange(event.block.location.add(0.0, 1.0, 0.0), event.block.type, event.block.data)
                    player.sendMessage("${Strings.PREFIX} §fLocalização marcada§f!")

                    locationMap[player.uniqueId] = event.block.location.add(0.0, 1.0, 0.0)
                }
            }
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        if (using(player)) {
            if (event.block.type == Material.NETHER_FENCE) {
                if (locationMap.containsValue(event.block.location)) {
                    player.sendMessage("${Strings.PREFIX} §fLocalização desmarcada§f!")
                    locationMap.remove(player.uniqueId)
                }
            }
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        if (using(player)) {
            if (player.itemInHand.type == Material.NETHER_FENCE &&
                    event.action.name.contains("LEFT")
            ) {
                event.isCancelled = true

                if (canUse) {
                    if (!inCooldown(player)) {
                        if (locationMap.containsKey(player.uniqueId)) {
                            player.teleport(locationMap[player.uniqueId])

                            player.fallDistance = 0F

                            player.playSound(player.location, Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F)

                            setCooldown(player, 15)
                        } else {
                            player.sendMessage("${Strings.PREFIX} §fVocê ainda não marcou uma localização§f!")
                        }
                    } else {
                        player.sendMessage(
                                "${Strings.PREFIX} §fAguarde §f${getCooldown(player)} §fPara usar novamente§e!"
                        )
                    }
                } else {
                    player.sendMessage(
                            "${Strings.PREFIX} §fVocê não pode usar seu kit agora§e!"
                    )
                }
            }
        }
    }
}