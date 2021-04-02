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
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class Kangaroo : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        return arrayListOf(
                GameManager.createItem("§f§lKit §eKangaroo", arrayOf(" "), Material.FIREWORK)
        )
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eKangaroo",
                arrayOf(" ", "§fSalte como um canguru!", " ", "§eClique para selecionar"),
                Material.FIREWORK
        )
    }

    override fun getPrice(): Int {
        return 0
    }

    override val cooldownMap: HashMap<UUID, Long> = HashMap()

    private val inUse: MutableList<UUID> = ArrayList()

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        if (using(player)) {
            if (player.itemInHand.type == Material.FIREWORK) {
                event.isCancelled = true

                if (!inUse.contains(player.uniqueId)) {
                    val velocity = player.eyeLocation.direction

                    if (player.isSneaking) {
                        velocity.multiply(2.5F)
                        velocity.y = 0.65
                    } else {
                        velocity.multiply(0.5F)
                        velocity.y = 1.0
                    }

                    player.fallDistance = -10F
                    player.velocity = velocity

                    inUse.add(player.uniqueId)
                }
            }
        }
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player

        if (inUse.contains(player.uniqueId)) {
            val block: Block = player.location.block

            if (block.type !== Material.AIR || block.getRelative(BlockFace.DOWN).type !== Material.AIR) {
                inUse.remove(player.uniqueId)
            }
        }
    }
}