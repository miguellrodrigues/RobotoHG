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
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import java.util.*
import kotlin.collections.HashMap

class Switcher : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        val itemStack = getIcon()
        itemStack.amount = 16

        return arrayListOf(itemStack)
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eSwitcher",
                arrayOf(" ", "§fTroque de lugar com outros jogadores!", " ", "§eClique para selecionar"),
                Material.SNOW_BALL
        )
    }

    override fun getPrice(): Int {
        return 5000
    }

    override val cooldownMap: HashMap<UUID, Long> = HashMap()

    @EventHandler
    fun onProjectileLaunch(event: ProjectileLaunchEvent) {
        val entity = event.entity

        if (entity.shooter !is Player)
            return

        val player = entity.shooter as Player

        if (using(player) && entity is Snowball) {
            if (!canUse)
                return

            entity.setMetadata("switcher", FixedMetadataValue(RobotoHG.INSTANCE, true))
        }
    }

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val entity = event.entity
        val damager = event.damager

        if (damager is Snowball && damager.hasMetadata("switcher")) {
            entity.teleport(damager.shooter as Player)
        }
    }
}