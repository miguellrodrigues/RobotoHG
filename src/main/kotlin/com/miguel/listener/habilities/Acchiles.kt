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
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

class Acchiles : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        return arrayListOf(ItemStack(Material.AIR))
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eAcchiles",
                arrayOf(" ", "§fLeve dano reduzido, exceto a items de madeira", " ", "§eClique para selecionar"),
                Material.WOOD_SWORD
        )
    }

    override fun getPrice(): Int {
        return 0
    }

    override val cooldownMap: HashMap<UUID, Long> = HashMap()

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val player = event.entity
        val damager = event.damager

        if (player is Player && damager is Player) {
            if (using(player)) {
                if (damager.itemInHand.type.name.contains("WOOD")) {
                    event.damage += 1.5
                } else {
                    event.damage -= 1.5
                }
            }
        }
    }
}