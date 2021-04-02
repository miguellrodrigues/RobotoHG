/*
 *
 *  * Copyright (©) RobotoHG-1.8
 *  *
 *  * Projeto desenvolvido por Miguell Rodrigues
 *  * Todos os direitos reservados
 *  *
 *  * Modificado em: 23/05/2020 21:05
 *
 */

package com.miguel.game.manager

import com.miguel.RobotoHG
import com.miguel.game.other.Sauron
import com.miguel.game.util.Effects
import com.miguel.packet.bossbar.BossBarManager
import com.miguel.packet.hologram.HologramManager
import com.miguel.packet.npc.NpcManager
import com.miguel.structures.JsonStructure
import com.miguel.util.Strings
import com.miguel.util.Structures
import com.miguel.util.Values
import net.minecraft.server.v1_8_R3.EnumParticle
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Chest
import org.bukkit.entity.EntityType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import kotlin.math.cos
import kotlin.math.sin

object FeastManager {

    private val chests: MutableList<Location> = ArrayList()

    private lateinit var feastTask: BukkitTask

    private var time = Values.SPAWN_FEAST
    //private val constant = (300.0 / time)

    val location: Location = Manager.getRandomLocation().subtract(0.0, 1.0, 0.0)

    private val feastLocation = Manager.getCenter(location.clone())

    private val feast = Structures.feast

    private var blocks: MutableList<JsonStructure.FutureBlock> = ArrayList()

    fun init() {
        feast.load(feastLocation.clone())

        blocks = feast.blocks

        blocks.forEach { t ->
            if (Material.getMaterial(t.material_name).name.contains("CHEST")) {
                chests.add(t.location)
            }
        }

        blocks.sortWith(Comparator.comparingDouble { value: JsonStructure.FutureBlock -> value.location.y })

        blocks.sortWith(Comparator.comparingDouble { value: JsonStructure.FutureBlock ->
            feastLocation.clone().distance(value.location)
        })
    }

    @Synchronized
    fun launch() {
        if (Values.FEAST_SPAWN)
            return

        if (Values.OLD_FEAST) {
            buildOldFeast()
        } else {
            buildFeast()
        }

        Values.FEAST_SPAWN = true

        val hologram = HologramManager.createMutableHologram("feast", feastLocation.clone().add(0.0, 3.0, 0.0))!!
        hologram.lines = arrayOf("§e§lFEAST", ".")
        hologram.show()

        BossBarManager.sendBossBar("§e§lFEAST §a(§f${feastLocation.clone().blockX}, §f${feastLocation.clone().blockY}, §f${feastLocation.clone().blockZ}§a)")

        val coneEffect = Effects.coneEffect(feastLocation.clone())

        val sauron = Sauron(feastLocation.clone().add(0.0, 10.0, 0.0), EnumParticle.SPELL)
        sauron.start()

        if (Values.EVENT_MODE) {
            val guardianNpc = NpcManager.spawnNpc(null, "Herobrine", "§c§lGUARDIAN", true, feastLocation.clone())

            guardianNpc.action.setSprinting().build()

            object : BukkitRunnable() {
                var degree = 0.0

                var toMove = feastLocation.clone().add(0.0, 1.3, 0.0)
                var toLook = feastLocation.clone().add(0.0, 3.5, 0.0)

                val radius = 8.35

                val increaseConstant = (15.0 / 180.0) / 2.0

                val t = time

                override fun run() {
                    if (degree <= 360) {
                        degree += (increaseConstant * (t - time)) + 2.0

                        val toRadians = Math.toRadians(degree)

                        val x: Double = radius * cos(toRadians)
                        val z: Double = radius * sin(toRadians)

                        toMove.add(x, 0.0, z)

                        guardianNpc.teleport(toMove.setDirection(toLook.clone().subtract(toMove).toVector()))

                        toMove.world.getNearbyEntities(toMove, 12.0, 10.0, 12.0).forEach { entity ->
                            if (entity.type == EntityType.ARMOR_STAND)
                                return@forEach

                            entity.velocity = entity.location.toVector().subtract(toMove.toVector()).normalize()
                        }

                        toMove.subtract(x, 0.0, z)
                    } else {
                        degree = 0.0
                    }

                    if (time == 0) {
                        guardianNpc.destroy()

                        cancel()
                    }
                }
            }.runTaskTimerAsynchronously(RobotoHG.INSTANCE, 0L, 0L)
        }

        val hologramTask = object : BukkitRunnable() {
            var degree = 0.0

            val toMove = feastLocation.clone()

            val radius = 8.5

            override fun run() {
                if (degree > 0) {
                    degree--

                    val toRadians = Math.toRadians(degree)

                    val x: Double = radius * cos(toRadians)
                    val z: Double = radius * sin(toRadians)

                    toMove.add(x, 0.0, z)

                    hologram.teleport(toMove)

                    toMove.subtract(x, 0.0, z)
                } else {
                    degree = 360.0
                }
            }
        }.runTaskTimerAsynchronously(RobotoHG.INSTANCE, 0L, 0L)

        feastTask = object : BukkitRunnable() {
            override fun run() {
                if (time > 0)
                    time--

                BossBarManager.updateAll()
                //BossBarManager.changeLife((constant * time).toFloat())

                hologram.updateDisplayName(1, "§fSpawn em §a${Manager.formatTime(time)}")

                if (time % 30 == 0) {
                    if (time == 0) {
                        GameManager.sendMessage("${Strings.PREFIX} §fO feast spawnow!")

                        placeChests()
                        chests.clear()

                        feast.destroy(false)

                        GameManager.playSound(Sound.ANVIL_LAND)

                        hologram.destroy()

                        coneEffect.cancel()

                        sauron.remove()

                        hologramTask.cancel()

                        BossBarManager.destroyAll()

                        cancel()
                    } else {
                        GameManager.sendCustomMessage(
                                "${Strings.PREFIX} §fO feast irá spawnar em §a(§f" +
                                        "${feastLocation.clone().blockX}§f, " +
                                        "§f${feastLocation.clone().blockY}§f, " +
                                        "§f${feastLocation.clone().blockZ}§a) §fEm §a${Manager.formatTime(time)}",
                                "feast",
                                "§fClique para apontar para o §aFeast §f!"
                        )
                    }
                }
            }
        }.runTaskTimer(RobotoHG.INSTANCE, 0L, 20L)
    }

    private fun buildFeast() {
        blocks.filter { futureBlock -> futureBlock.location.y <= feastLocation.y + 1.0 }
                .forEach { t -> Manager.removeBlocksBellow(t.location.block, (feastLocation.y + 25).toInt()) }

        Thread(Runnable {
            object : BukkitRunnable() {
                var x = -1

                override fun run() {
                    if (x != blocks.size - 1) {
                        x++

                        val block = blocks[x]

                        if (chests.contains(block.location))
                            return

                        block.place()
                    } else {
                        blocks.clear()
                        cancel()
                    }
                }
            }.runTaskTimer(RobotoHG.INSTANCE, 0L, 0L)
        }).start()
    }

    private fun buildOldFeast() {
        Manager.clearArea(feastLocation.clone(), 20)

        Manager.plainSphere(feastLocation.clone(), 20)
    }

    private fun placeChests() {
        chests.forEach { location ->
            location.block.type = Material.CHEST

            val chest = location.block.state as Chest
            GameManager.fillChest(chest, "feast")
        }
    }
}