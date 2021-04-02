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

package com.miguel.game.other

import com.miguel.RobotoHG
import com.miguel.game.util.Effects
import com.miguel.game.util.FlyingItem
import net.minecraft.server.v1_8_R3.EnumParticle
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

class Sauron(var origin: Location, val particle: EnumParticle) {

    private lateinit var task: BukkitTask

    private val sauronEye = FlyingItem(
            origin.add(0.0, 0.5, 0.0),
            "§e§lSAURON",
            ItemStack(Material.EYE_OF_ENDER)
    )

    fun start() {
        sauronEye.spawn()

        origin = sauronEye.armorStand.passenger.location.clone().add(0.0, 1.5, 0.0)

        val world = Bukkit.getWorld("world")

        task = object : BukkitRunnable() {
            override fun run() {
                val nearbyEntities = world.getNearbyEntities(origin, 15.0, 10.0, 15.0)

                if (nearbyEntities.isNotEmpty()) {
                    nearbyEntities.forEach { entity ->
                        if (entity.customName != sauronEye.item.customName && entity.type != EntityType.ARMOR_STAND) {
                            if (entity is Player) {
                                val helmet = entity.inventory.helmet
                                if (helmet != null && helmet.type == Material.PUMPKIN) {
                                    return@forEach
                                }
                            }

                            val initVector = origin.clone().toVector()

                            val destiny = entity.location

                            val distance = origin.distance(destiny)

                            val destinyVector = destiny.toVector()

                            val addVector = destinyVector.clone().subtract(initVector).normalize()

                            for (i in 0..distance.toInt()) {
                                initVector.add(addVector)

                                Effects.sendParticle(
                                        particle, initVector.toLocation(origin.world),
                                        0
                                )
                            }

                            entity.velocity = entity.velocity.subtract(origin.toVector()).setY(0.7).normalize()
                            entity.fireTicks = 150
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(RobotoHG.INSTANCE, 0L, 0L)
    }

    fun remove() {
        sauronEye.remove()
        task.cancel()
    }
}