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
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class BomberMan : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        val item = GameManager.createItem("§f§lKit §eBomberMan", arrayOf(" "), Material.GOLD_PLATE)
        item.amount = 8

        return arrayListOf(item)
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eBomberMan",
                arrayOf(" ", "§fPlante minas pela chão!", " ", "§eClique para selecionar"),
                Material.GOLD_PLATE
        )
    }

    override fun getPrice(): Int {
        return 0
    }

    override val cooldownMap: HashMap<UUID, Long> = HashMap()

    private val locations: MutableList<Location> = ArrayList()

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player

        if (using(player) && event.block.type == Material.GOLD_PLATE) {
            if (!canUse) {
                player.sendMessage(
                        "${Strings.PREFIX} §fVocê não pode usar seu kit agora§e!"
                )
                return
            }

            locations.add(event.block.location)
            player.sendMessage("${Strings.PREFIX} §fMina plantada §f!")
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val clickedBlock = event.clickedBlock

        if (event.action == Action.PHYSICAL && clickedBlock != null && locations.contains(clickedBlock.location)) {
            locations.remove(clickedBlock.location)
            clickedBlock.world.createExplosion(clickedBlock.location, 2.5F, true)
        }
    }
}