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
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

class Fireman : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        return arrayListOf(ItemStack(Material.AIR))
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eFireman",
                arrayOf(" ", "§fSeja imune ao fogo !", " ", "§eClique para selecionar"),
                Material.FLINT_AND_STEEL
        )
    }

    override fun getPrice(): Int {
        return 0
    }

    override val cooldownMap: HashMap<UUID, Long> = HashMap()

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        val player = event.entity

        if (player is Player) {
            if (using(player) && (event.cause.name.contains("FIRE") || event.cause == EntityDamageEvent.DamageCause.LAVA)) {
                event.isCancelled = true
            }
        }
    }
}