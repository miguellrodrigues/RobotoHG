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
import com.miguel.util.Strings
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.abs

class EnderMage : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        return arrayListOf(
                GameManager.createItem("§f§lKit §eEnderMage", arrayOf(""), Material.NETHER_BRICK_ITEM)
        )
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eEnderMage",
                arrayOf(" ", "§fCrie um portal até você", " ", "§eClique para selecionar"),
                Material.NETHER_BRICK_ITEM
        )
    }

    override fun getPrice(): Int {
        return 0
    }

    override val cooldownMap: HashMap<UUID, Long> = HashMap()

    private val invencible: MutableList<UUID> = ArrayList()

    private var teleported = false
    private var portalLocation: Location? = null

    private var invencibleTime = 6

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        if (using(player)) {
            if (player.itemInHand.type == Material.NETHER_BRICK_ITEM) {
                event.isCancelled = true

                if (canUse) {
                    if (!inCooldown(player)) {
                        if (event.action == Action.RIGHT_CLICK_BLOCK) {
                            if (portalLocation == null) {
                                val clickedBlock = event.clickedBlock

                                val initialType = clickedBlock.type

                                clickedBlock.type = Material.ENDER_STONE
                                portalLocation = clickedBlock.location

                                player.inventory.remove(player.itemInHand)

                                object : BukkitRunnable() {
                                    override fun run() {
                                        if (invencibleTime > 0)
                                            invencibleTime--

                                        if (invencibleTime == 0 || teleported) {
                                            if (teleported) {
                                                invencible.add(player.uniqueId)

                                                player.teleport(portalLocation!!.clone().add(0.0, 1.5, 0.0))
                                                player.playSound(
                                                        player.location,
                                                        Sound.ENDERMAN_TELEPORT,
                                                        1.0F,
                                                        1.0F
                                                )

                                                invincibleTask()

                                                teleported = false
                                            }

                                            portalLocation = null

                                            clickedBlock.type = initialType
                                            player.inventory.addItem(getItems()[0])

                                            invencibleTime = 6

                                            cancel()
                                        }
                                    }
                                }.runTaskTimer(RobotoHG.INSTANCE, 0L, 20L)
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
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player

        if (portalLocation != null && canTeleport(player)) {
            invencible.add(player.uniqueId)

            player.teleport(portalLocation!!.clone().add(0.0, 1.5, 0.0))
            player.fallDistance = 0F

            player.playSound(player.location, Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F)

            teleported = true
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        val entity = event.entity

        if (entity is Player) {
            if (invencible.contains(entity.uniqueId)) {
                event.isCancelled = true
            }
        }
    }

    private fun invincibleTask(): BukkitTask {
        invencible.forEach { uuid ->
            Bukkit.getPlayer(uuid).sendMessage("§cVocê está invencível por 5 segundos!")
        }

        return object : BukkitRunnable() {
            override fun run() {
                invencible.clear()
            }
        }.runTaskLater(RobotoHG.INSTANCE, 100L)
    }

    private fun canTeleport(player: Player): Boolean {
        return abs(portalLocation!!.x - player.location.x) < 5.0 && abs(portalLocation!!.z - player.location.z) < 5.0 && abs(
                portalLocation!!.y - player.location.y
        ) >= 3.5
    }
}