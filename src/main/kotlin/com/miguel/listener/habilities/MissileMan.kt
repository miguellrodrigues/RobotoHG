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
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MissileMan : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        val bombMissile = GameManager.createItem(
                "§eExplosivo",
                arrayOf(" ", "§fEsta flecha explode ao antingir um objeto"),
                Material.ARROW
        )

        bombMissile.amount = 3

        val torpedo = GameManager.createItem(
                "§eTorpedo",
                arrayOf(" ", "§fEste torpedo permite que você monte-o", " ", "§eClique para selecionar"),
                Material.SNOW_BALL
        )
        torpedo.amount = 4

        val launcher = GameManager.createItem("§f§lKit §eMissile", arrayOf(""), Material.BOW)

        return arrayListOf(launcher, bombMissile, torpedo)
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eMissileMan",
                arrayOf(" ", "§fGanhe um lançador de mísseis !"),
                Material.ARROW
        )
    }

    override fun getPrice(): Int {
        return 0
    }

    override val cooldownMap: HashMap<UUID, Long> = HashMap()

    private val missiles: MutableList<Entity> = ArrayList()

    @EventHandler
    fun onProjectileLaunch(event: ProjectileLaunchEvent) {
        val entity = event.entity
        val shooter = entity.shooter

        if (shooter is Player) {
            if (using(shooter)) {
                if (entity is Arrow) {
                    missiles.add(entity)
                } else if (entity is Snowball) {
                    entity.passenger = shooter
                }
            }
        }
    }

    @EventHandler
    fun onProjectileHit(event: ProjectileHitEvent) {
        val entity = event.entity

        if (missiles.contains(entity)) {
            missiles.remove(entity)

            entity.world.createExplosion(entity.location, 2.0F, true)

            entity.remove()
        }
    }
}