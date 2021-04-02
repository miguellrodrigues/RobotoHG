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
import org.bukkit.Material
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

class C4 : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        return arrayListOf(
                GameManager.createItem("§f§fKit §e§lC4", arrayOf(""), Material.SLIME_BALL)
        )
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eC4",
                arrayOf(" ", "§fSeja um verdadeiro homem bomba!", " ", "§eClique para selecionar"),
                Material.SLIME_BALL
        )
    }

    override fun getPrice(): Int {
        return 0
    }

    override val cooldownMap: HashMap<UUID, Long> = HashMap()

    private val bomb: HashMap<UUID, Item> = HashMap()

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        if (using(player)) {
            if (player.itemInHand.type == Material.SLIME_BALL) {
                if (canUse) {
                    if (!inCooldown(player)) {
                        if (event.action.name.contains("RIGHT")) {
                            if (bomb.containsKey(player.uniqueId)) {
                                val item = bomb[player.uniqueId]!!

                                item.world.createExplosion(item.location, 2.5F, false)
                                item.remove()

                                bomb.remove(player.uniqueId)
                                setCooldown(player, 30)
                            } else {
                                player.sendMessage("${Strings.PREFIX} §fVocê ainda não plantou a bomba§f!")
                            }
                        } else {
                            if (bomb.containsKey(player.uniqueId)) {
                                val item = bomb[player.uniqueId]!!
                                item.remove()
                            }

                            val item = player.world.dropItem(player.eyeLocation, ItemStack(Material.TNT))
                            item.pickupDelay = 9999
                            item.velocity = player.eyeLocation.direction

                            bomb[player.uniqueId] = item

                            player.sendMessage("${Strings.PREFIX} §fVocê plantou a bomba§f!")
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