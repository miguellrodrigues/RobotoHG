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
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Gladiator : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        return arrayListOf(
                GameManager.createItem("§f§lKit §eGladiator", emptyArray(), Material.IRON_FENCE)
        )
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eGladiator",
                arrayOf(" ", "§fPuxe seus inimigos para uma arena", " ", "§eClique para selecionar"),
                Material.IRON_FENCE
        )
    }

    override fun getPrice(): Int {
        return 0
    }

    override val cooldownMap: HashMap<UUID, Long> = HashMap()

    private val gladiator: HashMap<UUID, GladiatorArena> = hashMapOf()

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        val player = event.player

        val rightClicked = event.rightClicked

        if (using(player) && rightClicked is Player && player.itemInHand.type == Material.IRON_FENCE) {
            if (player.uniqueId !in gladiator) {
                val glad = GladiatorArena(player, rightClicked, arrayOf(20, 15, 20))
                glad.init()

                gladiator[player.uniqueId] = glad
            }
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        if (player.uniqueId in gladiator) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val entity = event.entity
        val killer = entity.killer

        if (killer is Player) {
            if (entity.uniqueId in gladiator || killer.uniqueId in gladiator) {

                val glad: GladiatorArena

                if (entity.uniqueId in gladiator) {
                    glad = gladiator[entity.uniqueId]!!

                    gladiator.remove(entity.uniqueId)
                } else {
                    glad = gladiator[killer.uniqueId]!!

                    gladiator.remove(killer.uniqueId)
                }

                glad.blocks.forEach { block ->
                    block.type = Material.AIR
                }

                val location = killer.location.subtract(.0, 49.0, .0)

                killer.teleport(location)
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player

        if (player.uniqueId in gladiator) {
            val glad = gladiator[player.uniqueId]!!

            if (glad.owner == player) {
                glad.enemy.teleport(glad.enemy.location.subtract(.0, 49.0, .0))
            } else {
                glad.owner.teleport(glad.owner.location.subtract(.0, 49.0, .0))
            }

            glad.blocks.forEach { block ->
                block.type = Material.AIR
            }

            gladiator.remove(player.uniqueId)
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player

        val block = event.block

        if (using(player) && block.type == Material.IRON_FENCE) {
            event.isCancelled = true
        }
    }

    private class GladiatorArena(val owner: Player, val enemy: Player, val dimensions: Array<Int>) {
        val blocks: MutableList<Block> = ArrayList()

        fun init() {
            val location = owner.location.clone().add(0.0, 50.0, 0.0)

            if (dimensions.size == 3) {
                for (x in 0..dimensions[0]) {
                    for (y in 0..dimensions[1]) {
                        for (z in 0..dimensions[2]) {
                            if (x == 0 || y == 0 || z == 0 || x == dimensions[0] || y == dimensions[1] || z == dimensions[2]) {
                                val block = location.clone().add(x.toDouble(), y.toDouble(), z.toDouble()).block

                                block.type = Material.GLASS

                                blocks.add(block)
                            }
                        }
                    }
                }

                owner.teleport(location.clone().add(1.0, 1.0, 1.0))
                enemy.teleport(location.clone().add(19.0, 1.0, 19.0))
            }
        }
    }
}