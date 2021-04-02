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

class Viking : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        return arrayListOf(ItemStack(Material.AIR))
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eViking",
                arrayOf(" ", "§fUse machados para causar mais dano!", " ", "§eClique para selecionar"),
                Material.IRON_AXE
        )
    }

    override fun getPrice(): Int {
        return 0
    }

    override val cooldownMap: HashMap<UUID, Long> = HashMap()

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val entity = event.entity
        val damager = event.damager

        if (entity is Player && damager is Player) {
            if (using(damager)) {
                if (damager.itemInHand.type.name.toLowerCase().contains("axe")) {
                    event.damage += getDamage(damager.itemInHand.type)
                }
            }
        }
    }

    private fun getDamage(type: Material): Double {
        var damage = 0.0

        when (type) {
            Material.WOOD_AXE -> {
                damage = 0.5
            }

            Material.STONE_AXE -> {
                damage = 1.0
            }

            Material.GOLD_AXE -> {
                damage = 1.5
            }

            Material.IRON_AXE -> {
                damage = 2.0
            }

            Material.DIAMOND_AXE -> {
                damage = 2.5
            }
        }

        return damage
    }
}