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

package com.miguel.game.kit

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.TimeUnit

abstract class Kit : Listener {

    abstract fun getItems(): MutableList<ItemStack>

    abstract fun getIcon(): ItemStack

    abstract fun getPrice(): Int

    abstract val cooldownMap: HashMap<UUID, Long>

    var canUse: Boolean = false

    fun getName(): String {
        return javaClass.simpleName
    }

    fun hasItems(): Boolean {
        return getItems().isNotEmpty() && getItems()[0].type != Material.AIR
    }

    fun using(player: Player): Boolean {
        return KitManager.getKits(player).contains(this)
    }

    fun setCooldown(player: Player, time: Long) {
        cooldownMap[player.uniqueId] = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(time)
    }

    fun inCooldown(player: Player): Boolean {
        return cooldownMap.containsKey(player.uniqueId) && cooldownMap[player.uniqueId]!! > System.currentTimeMillis()
    }

    fun getCooldown(player: Player): String? {
        if (inCooldown(player)) {
            val cooldown = TimeUnit.MILLISECONDS
                    .toSeconds(cooldownMap[player.uniqueId]!! - System.currentTimeMillis())

            var seconds = " segundo"

            if (cooldown > 1L) seconds += "s"

            if (cooldown > 0L) return cooldown.toString() + seconds

            if (cooldown == 0L) return "0 segundos"
        }

        return null
    }
}