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
import com.miguel.packet.PacketUtil
import com.miguel.util.Strings
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.collections.HashMap

class GravityGun : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        return arrayListOf(
                GameManager.createItem(
                        "§f§lKit §eGravityGun",
                        emptyArray(),
                        Material.BLAZE_ROD
                )
        )
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eGravityGun",
                arrayOf(" ", "§fUse sua portal gun para mover inimigos!", " ", "§eClique para selecionar"),
                Material.BLAZE_ROD
        )
    }

    override fun getPrice(): Int {
        return 0
    }

    override val cooldownMap: HashMap<UUID, Long> = HashMap()

    private val gravityEntities: HashMap<UUID, GravityEntity> = HashMap()

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        if (using(player) && player.itemInHand.type == Material.BLAZE_ROD) {
            if (canUse) {
                if (!inCooldown(player)) {

                    when (event.action) {
                        Action.LEFT_CLICK_AIR -> {
                            if (player.uniqueId in gravityEntities) {
                                val gravityEntity = gravityEntities[player.uniqueId]!!

                                gravityEntity.launch()

                                gravityEntities.remove(player.uniqueId)

                                setCooldown(player, 30)
                            }
                        }

                        else -> {
                        }
                    }

                } else {
                    player.sendMessage(
                            "${Strings.PREFIX} §fAguarde §f${getCooldown(player)} §fPara usar novamente§e!"
                    )
                }
            } else {
                player.sendMessage(
                        "${Strings.PREFIX} §fVocê não pode usar seu kit agora§e!"
                )
            }
        }
    }

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        val player = event.player

        if (using(player) && player.itemInHand.type == Material.BLAZE_ROD) {
            val rightClicked = event.rightClicked

            if (canUse) {
                if (!inCooldown(player)) {

                    if (player.uniqueId !in gravityEntities) {
                        val gravityEntity = GravityEntity(player, rightClicked)

                        gravityEntities[player.uniqueId] = gravityEntity
                    }
                } else {
                    player.sendMessage(
                            "${Strings.PREFIX} §fAguarde §f${getCooldown(player)} §fPara usar novamente§e!"
                    )
                }
            } else {
                player.sendMessage(
                        "${Strings.PREFIX} §fVocê não pode usar seu kit agora§e!"
                )
            }
        }
    }

    @EventHandler
    fun onPlayerItemHeld(event: PlayerItemHeldEvent) {
        val player = event.player

        if (player.uniqueId in gravityEntities) {
            val gravityEntity = gravityEntities[player.uniqueId]!!

            gravityEntity.launch()

            gravityEntities.remove(player.uniqueId)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player

        if (player.uniqueId in gravityEntities) {
            val gravityEntity = gravityEntities[player.uniqueId]!!

            gravityEntity.launch()

            gravityEntities.remove(player.uniqueId)
        }
    }

    private class GravityEntity(val player: Player, val entity: Entity) : BukkitRunnable() {

        init {
            this.runTaskTimer(RobotoHG.INSTANCE, 0L, 0L)
        }

        override fun run() {
            setPos(getNextPos())

            if (entity.location.distance(player.location) > 50) {
                cancel()
            }
        }

        fun setPos(location: Location) {
            val handle = getHandle()

            handle.locX = location.x
            handle.locY = location.y
            handle.locZ = location.z

            PacketUtil.sendPacket(PacketPlayOutEntityTeleport(getHandle()))
        }

        fun launch() {
            entity.velocity = entity.location.toVector().subtract(player.location.toVector()).normalize().multiply(1.3)

            cancel()
        }

        fun getNextPos(): Location {
            return player.eyeLocation.clone().add(player.eyeLocation.direction.multiply(4))
        }

        fun getHandle(): net.minecraft.server.v1_8_R3.Entity {
            return (entity as CraftEntity).handle
        }
    }
}