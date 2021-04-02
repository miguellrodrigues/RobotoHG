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
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.abs

class Launcher : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        val stack = GameManager.createItem("§f§lKit §eLauncher", emptyArray(), Material.SPONGE)
        stack.amount = 16

        return arrayListOf(stack)
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eLauncher",
                arrayOf(" ", "§fUse blocos para pular bem alto!", " ", "§eClique para selecionar"),
                Material.SPONGE
        )
    }

    override fun getPrice(): Int {
        return 0
    }

    override val cooldownMap = HashMap<UUID, Long>()

    private val blocks: MutableList<Block> = ArrayList()

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player

        if (using(player)) {
            if (event.block.type == Material.SPONGE) {
                blocks.add(event.block)
            }
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        val block = event.block

        if (using(player)) {
            if (block.type == Material.SPONGE) {
                event.isCancelled = true

                block.type = Material.AIR

                player.inventory.addItem(
                        GameManager.createItem("§f§lKit §eLauncher", emptyArray(), Material.SPONGE)
                )

                if (blocks.contains(block))
                    blocks.remove(block)
            }
        }
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player

        if (using(player)) {
            val to = event.to.block.getRelative(BlockFace.DOWN)

            if (to in blocks) {
                val pitch = abs(player.location.pitch)

                val velocity = if (pitch > 50) {
                    Vector(.0, 2.0, .0)
                } else {
                    Vector(.0, 2.0, .0).add(player.eyeLocation.direction.multiply(2.2))
                }

                player.velocity = velocity
            }
        }
    }
}