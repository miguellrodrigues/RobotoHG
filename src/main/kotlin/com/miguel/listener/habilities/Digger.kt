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

import com.miguel.RobotoHG
import com.miguel.game.kit.Kit
import com.miguel.game.manager.GameManager
import com.miguel.game.manager.Manager
import com.miguel.util.Strings
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.collections.HashMap

class Digger : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        val item = GameManager.createItem("§f§lKit §eDigger", arrayOf(" "), Material.DRAGON_EGG)
        item.amount = 4

        return arrayListOf(item)
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eDigger",
                arrayOf(" ", "§fAbra crateras no chão", " ", "§eClique para selecionar"),
                Material.DRAGON_EGG
        )
    }

    override fun getPrice(): Int {
        return 0
    }

    override val cooldownMap: HashMap<UUID, Long> = HashMap()

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        val block = event.block

        if (using(player) && block.type == Material.DRAGON_EGG) {
            if (!canUse) {
                event.isCancelled = true

                player.sendMessage(
                        "${Strings.PREFIX} §fVocê não pode usar seu kit agora§e!"
                )

                return
            }

            block.type = Material.AIR

            player.sendMessage("${Strings.PREFIX} §fVocê colocou o ovo, fuja §f!")

            object : BukkitRunnable() {

                val blocks = Manager.sphere(block.location, 8)

                override fun run() {
                    blocks.forEach { location ->
                        location.block.type = Material.AIR
                    }
                }
            }.runTaskLater(RobotoHG.INSTANCE, 60L)
        }
    }
}