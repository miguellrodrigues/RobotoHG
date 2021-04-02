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

package com.miguel.packet.hologram

import com.miguel.packet.PacketUtil
import net.minecraft.server.v1_8_R3.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.entity.Player

class Hologram {

    lateinit var lines: Array<String>

    lateinit var location: Location

    private val world = (Bukkit.getWorld("world") as CraftWorld).handle

    var player: Player? = null

    private val viewers: MutableList<Player> = ArrayList()

    private val entities: MutableList<EntityArmorStand> = ArrayList()

    fun show() {
        if (player == null) {
            lines.forEach { line ->
                showLine(line, location.subtract(.0, .35, .0))
            }
        } else {
            if (!viewers.contains(player!!)) {
                viewers.add(player!!)

                lines.forEach { line ->
                    showLine(line, location.subtract(.0, .35, .0))
                }
            }
        }
    }

    fun destroy() {
        entities.forEach { entity ->
            entity.bukkitEntity.remove()

            PacketUtil.sendPacket(PacketPlayOutEntityDestroy(entity.id))
        }

        viewers.clear()
    }

    fun updateDisplayName(index: Int, value: String) {
        val entity = entities[index]

        entity.customName = value

        PacketUtil.sendPacket(PacketPlayOutEntityMetadata(entity.id, entity.dataWatcher, true))
    }

    fun destroy(player: Player) {
        if (viewers.contains(player)) {
            entities.forEach { entity ->
                entity.bukkitEntity.remove()

                PacketUtil.sendPacket(player, PacketPlayOutEntityDestroy(entity.id))
            }

            viewers.remove(player)
        }
    }

    private fun showLine(text: String, location: Location) {
        val armorStand = EntityArmorStand(world)

        armorStand.customName = text
        armorStand.customNameVisible = true

        armorStand.setGravity(false)
        armorStand.isInvisible = true

        armorStand.setLocation(
                location.x,
                location.y,
                location.z,
                location.pitch,
                location.yaw
        )

        entities.add(armorStand)

        PacketUtil.sendPacket(PacketPlayOutSpawnEntityLiving(armorStand))
    }

    fun teleport(location: Location) {
        entities.forEach { entity ->
            val packet = PacketPlayOutEntityTeleport(entity)

            entity.setLocation(
                    location.x,
                    entity.locY,
                    location.z,
                    location.pitch,
                    location.yaw
            )

            PacketUtil.sendPacket(packet)
        }
    }
}