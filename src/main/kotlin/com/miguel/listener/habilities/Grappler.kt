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
import net.minecraft.server.v1_8_R3.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSnowball
import org.bukkit.entity.Entity
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerLeashEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*
import kotlin.collections.HashMap

class Grappler : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        return arrayListOf(GameManager.createItem(
                "§f§lKit §eGrappler",
                arrayOf(" "),
                Material.LEASH
        ))
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eGrappler",
                arrayOf(" ", "§fUse sua corda para se locomover", " ", "§eClique para selecionar"),
                Material.LEASH
        )
    }

    override fun getPrice(): Int {
        return 0
    }

    override val cooldownMap: HashMap<UUID, Long> = HashMap()

    private val hooks: HashMap<UUID, GrapplerHook> = HashMap()

    @EventHandler
    fun onPlayerItemHeld(event: PlayerItemHeldEvent) {
        val player = event.player

        if (hooks.containsKey(player.uniqueId)) {
            val hook = hooks[player.uniqueId]!!

            hook.die()
            hook.bukkitEntity.remove()

            hooks.remove(player.uniqueId)
        }
    }

    @EventHandler
    fun onPlayerLeashEntity(event: PlayerLeashEntityEvent) {
        val player = event.player

        if (using(player)) {
            event.isCancelled = true

            player.updateInventory()
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        val damager = event.damager

        if (damager is Snowball && damager.customName == ".") {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        if (using(player)) {
            if (player.itemInHand.type == Material.LEASH) {
                event.isCancelled = true

                if (event.action.name.contains("LEFT")) {
                    if (hooks.containsKey(player.uniqueId)) {
                        hooks[player.uniqueId]?.die()
                    }

                    val hook = GrapplerHook((player.world as CraftWorld), (player as CraftPlayer).handle)

                    val direction = player.eyeLocation.direction.multiply(5.0)

                    hook.spawn()

                    hook.move(
                            direction.x,
                            direction.y,
                            direction.z
                    )

                    hooks[player.uniqueId] = hook

                } else {
                    if (hooks.containsKey(player.uniqueId)) {
                        val grapplerHook = hooks[player.uniqueId]!!

                        if (grapplerHook.isHooked) {
                            if (grapplerHook.locY <= 200) {
                                val grapplerHookLocation = grapplerHook.bukkitEntity.location

                                val distance = grapplerHookLocation.distance(player.location)

                                val y: Double
                                val factor: Double

                                if (grapplerHook.hookedEntity != null) {
                                    factor = (1.1 * distance)

                                    y = if (grapplerHookLocation.blockY < player.location.blockY) {
                                        -1.0
                                    } else {
                                        ((0.99 * distance) *
                                                (grapplerHookLocation.y - player.location.y)) / distance
                                    }
                                } else {
                                    factor = (1.035 * distance)

                                    y = if (grapplerHookLocation.blockY < player.location.blockY) {
                                        0.1
                                    } else {
                                        ((0.93 * distance) *
                                                (grapplerHookLocation.y - player.location.y)) / distance
                                    }
                                }

                                val velocity =
                                        grapplerHookLocation.toVector().subtract(player.location.toVector())
                                                .multiply(factor / distance)
                                                .setY(y)
                                                .normalize()
                                                .multiply(2.3)

                                player.velocity = velocity

                                if (grapplerHookLocation.y > player.location.y && player.fallDistance > 20.0) {
                                    player.velocity = Vector()
                                }

                                player.fallDistance = -10.0f

                                player.playSound(player.location, Sound.STEP_GRAVEL, 1.0F, 1.0F)
                            } else {
                                player.sendMessage("§cSua corda está muito alta!")
                            }
                        }
                    }
                }
            }
        }
    }

    private class GrapplerHook(world: CraftWorld, entityPlayer: EntityPlayer) :
            EntityFishingHook(world.handle, entityPlayer) {

        private lateinit var snowBall: Snowball
        private lateinit var controller: EntitySnowball

        var isHooked = false

        private val player = entityPlayer.bukkitEntity

        private val wrd = world

        var hookedEntity: Entity? = null

        override fun t_() {
            controller.world.world.livingEntities.forEach { entity ->
                if (entity != player && entity != controller) {
                    if (entity.location.distance(controller.bukkitEntity.location) < 2.0) {
                        this.controller.die()

                        hookedEntity = entity
                        isHooked = true

                        locX = entity.location.x
                        locY = entity.location.y
                        locZ = entity.location.z

                        this.hooked = (entity as CraftEntity).handle

                        motX = 0.0
                        motY = 0.04
                        motZ = 0.0

                        return
                    }
                }
            }

            if (hookedEntity != null) {
                locX = hooked.locX
                locY = hooked.locY
                locZ = hooked.locZ

                motX = 0.0
                motY = 0.04
                motZ = 0.0

                isHooked = true
            } else {
                if (controller.dead) {
                    isHooked = true

                    locX = this.controller.locX
                    locY = this.controller.locY
                    locZ = this.controller.locZ
                }
            }
        }

        fun spawn() {
            snowBall = player.launchProjectile(Snowball::class.java)
            snowBall.velocity = snowBall.velocity.multiply(2.25)
            snowBall.customName = "."
            snowBall.isCustomNameVisible = false

            this.controller = (snowBall as CraftSnowball).handle

            sendPacket(PacketPlayOutEntityDestroy(controller.id))

            wrd.handle.addEntity(this)

            this.controller.passenger = this
        }

        fun sendPacket(packet: Packet<*>) {
            Bukkit.getOnlinePlayers().forEach { player ->
                (player as CraftPlayer).handle.playerConnection.sendPacket(packet)
            }
        }
    }
}