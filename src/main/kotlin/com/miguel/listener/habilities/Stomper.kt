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
import com.miguel.game.kit.KitManager
import com.miguel.game.manager.GameManager
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

class Stomper : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        return arrayListOf(ItemStack(Material.AIR))
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eStomper",
                arrayOf(" ", "§fDerrote seus inimigos usando as leis de newton", " ", "§eClique para selecionar"),
                Material.DIAMOND_BOOTS
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
            if (using(player) && event.cause == EntityDamageEvent.DamageCause.FALL) {
                val damage = event.damage

                event.damage = 2.0

                val nearbyEntities = player.getNearbyEntities(7.0, 5.0, 7.0)

                if (nearbyEntities.isNotEmpty()) {
                    nearbyEntities.forEach { entity ->
                        if (entity is Player) {
                            if (KitManager.getKits(entity).contains(AntiStomper())) {
                                entity.damage(2.5)
                            } else {
                                if (entity.isSneaking) {
                                    entity.damage(2.5)
                                } else {
                                    if (player.fallDistance > 20) {
                                        entity.damage(damage)
                                    } else {
                                        entity.damage(damage * 0.6)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}