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

package com.miguel.game.util

import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack

class FlyingItem(val location: Location, private val text: String, private val itemStack: ItemStack) {

    lateinit var armorStand: ArmorStand

    lateinit var item: Item

    private var height = -1.3

    fun remove() {
        this.item.remove()

        this.armorStand.remove()
        this.armorStand.damage(50.0)

        this.height = 0.0
    }

    fun teleport(location: Location) {
        armorStand.teleport(location)
    }

    fun spawn() {
        this.location.y = this.location.y + this.height

        this.armorStand = this.location.world.spawn(this.location, ArmorStand::class.java)

        this.armorStand.setGravity(false)
        this.armorStand.isVisible = false

        this.item = this.location.world.dropItem(this.location, this.itemStack)
        this.item.pickupDelay = 2147483647

        this.item.customName = text
        this.item.isCustomNameVisible = true

        this.armorStand.passenger = this.item
    }

    fun getHeight(): Double {
        return this.height
    }

    fun setHeight(height: Double) {
        this.height = height - 1.3
        this.location.y = this.location.y + this.height
    }
}