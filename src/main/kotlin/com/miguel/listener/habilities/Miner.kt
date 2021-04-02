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
import com.miguel.game.manager.Manager
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*
import kotlin.collections.HashMap

class Miner : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        val item = GameManager.createItem(
                "§f§lKit §eMiner",
                emptyArray(),
                Material.STONE_PICKAXE
        )

        item.addEnchantment(Enchantment.DIG_SPEED, 2)

        return arrayListOf(
                item,
                ItemStack(Material.APPLE, 4)
        )
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eMiner",
                arrayOf(" ", "§fUse sua picareta para minera ferro muito rápido!", " ", "§eClique para selecionar"),
                Material.STONE_PICKAXE
        )
    }

    override fun getPrice(): Int {
        return 0
    }

    override val cooldownMap: HashMap<UUID, Long> = HashMap()

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        if (using(player) && player.itemInHand.type == Material.STONE_PICKAXE) {
            val block = event.block

            if (block.type == Material.IRON_ORE) {
                val connectedBlocks = Manager.getConnectedBlocks(block, Material.IRON_ORE)

                connectedBlocks.forEach { t -> t.breakNaturally() }

                /*val blockFaces = BlockFace.values()

                blockFaces.forEach { face ->
                    var relative = block.getRelative(face)

                    while (relative.type == Material.IRON_ORE) {
                        relative.breakNaturally()
                        relative = relative.getRelative(face)
                    }
                }*/
            }
        }
    }

    @EventHandler
    fun onPlayerItemConsume(event: PlayerItemConsumeEvent) {
        val player = event.player

        if (using(player)) {
            if (event.item.type == Material.APPLE) {
                player.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, 200, 1), true)
            }
        }
    }
}