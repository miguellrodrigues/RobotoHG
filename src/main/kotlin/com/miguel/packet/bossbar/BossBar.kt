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

package com.miguel.packet.bossbar

import com.miguel.RobotoHG
import com.miguel.packet.PacketUtil
import net.minecraft.server.v1_8_R3.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import kotlin.math.roundToInt

class BossBar(val player: Player, private var text: String) {

    private var timeOut = -1

    private lateinit var wither: EntityWither

    private var task: BukkitTask? = null

    private val world = Bukkit.getWorld("world")

    private var location = getLocation()

    fun destroy() {
        val packet = PacketPlayOutEntityDestroy(wither.id)

        PacketUtil.sendPacket(player, packet)

        task?.cancel()
    }

    fun spawn() {
        task?.cancel()

        this.wither = EntityWither(
                (world as CraftWorld).handle
        )

        this.wither.setLocation(
                location.x,
                location.y,
                location.z,
                location.pitch,
                location.yaw
        )

        this.wither.motX = 0.0
        this.wither.motY = 0.0
        this.wither.motZ = 0.0

        this.wither.customName = text
        this.wither.customNameVisible = true

        this.wither.isInvisible = true

        PacketUtil.sendPacket(player, PacketPlayOutSpawnEntityLiving(wither))

        Thread(Runnable {
            task = object : BukkitRunnable() {
                override fun run() {
                    if (timeOut != -1) {
                        if (timeOut > 0)
                            timeOut--

                        if (timeOut == 0) {
                            destroy()
                            cancel()
                        }
                    } else {
                        update()
                    }
                }
            }.runTaskTimer(RobotoHG.INSTANCE, 0L, 0L)
        }).start()
    }

    fun changeHealth(health: Float) {
        this.wither.health = health

        PacketUtil.sendPacket(player, PacketPlayOutEntityMetadata(wither.id, wither.dataWatcher, true))
    }

    fun update() {
        this.location = getLocation()

        wither.setLocation(
                location.x,
                location.y,
                location.z,
                0.0F,
                0.0F
        )

        PacketUtil.sendPacket(player, PacketPlayOutEntityTeleport(wither))
    }

    private fun getLocation(): Location {
        var loc = player.location
        val pitch = loc.pitch

        when {
            pitch >= 55.0f -> {
                loc.add(0.0, -32.0, 0.0)
            }
            pitch <= -55.0f -> {
                loc.add(0.0, 32.0, 0.0)
            }
            else -> {
                loc = loc.block.getRelative(getDirection(loc), 48).location
            }
        }

        return loc
    }

    private fun getDirection(loc: Location): BlockFace? {
        val dir = (loc.yaw / 90.0f).roundToInt().toFloat()
        if (dir == -4.0f || dir == 0.0f || dir == 4.0f) {
            return BlockFace.SOUTH
        }
        if (dir == -1.0f || dir == 3.0f) {
            return BlockFace.EAST
        }
        if (dir == -2.0f || dir == 2.0f) {
            return BlockFace.NORTH
        }
        return if (dir == -3.0f || dir == 1.0f) {
            BlockFace.WEST
        } else null
    }
}