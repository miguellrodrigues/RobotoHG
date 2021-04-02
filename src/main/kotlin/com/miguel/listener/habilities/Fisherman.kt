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
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

class Fisherman : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        return arrayListOf(
                GameManager.createItem(
                        "§f§lKit §eFisherman",
                        arrayOf(""),
                        Material.FISHING_ROD
                )
        )
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eFisherman",
                arrayOf(" ", "§fFisgue seus inimigos!", " ", "§eClique para selecionar"),
                Material.FISHING_ROD
        )
    }

    override fun getPrice(): Int {
        return 0
    }

    override val cooldownMap: HashMap<UUID, Long> = HashMap()

    @EventHandler
    fun onPlayerFish(event: PlayerFishEvent) {
        val player = event.player

        if (using(player)) {
            if (canUse) {
                if (event.caught is Player) {
                    event.caught.teleport(player.location)
                }
            } else {
                player.sendMessage(
                        "${Strings.PREFIX} §fVocê não pode usar seu kit agora!"
                )
            }
        }
    }

    @EventHandler
    fun onItemConsume(event: PlayerItemConsumeEvent) {
        val player = event.player

        if (using(player)) {
            if (event.item == getItems()[0]) {
                event.isCancelled = true
            }
        }
    }
}