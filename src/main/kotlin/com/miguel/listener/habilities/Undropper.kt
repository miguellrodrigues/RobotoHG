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
import com.miguel.game.util.Effects
import com.miguel.util.Strings
import net.minecraft.server.v1_8_R3.EnumParticle
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Undropper : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        return arrayListOf(
                GameManager.createItem(
                        "§f§lKit §eUndropper",
                        emptyArray(),
                        Material.BLAZE_ROD
                )
        )
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eUndropper",
                arrayOf(" ", "§fImpeça seu inimigo de dropar items!", " ", "§eClique para selecionar"),
                Material.DROPPER
        )
    }

    override fun getPrice(): Int {
        return 0
    }

    override val cooldownMap: HashMap<UUID, Long> = HashMap()

    private val cantDrop: MutableList<UUID> = ArrayList()

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        val player = event.player

        val rightClicked = event.rightClicked

        if (using(player) && rightClicked is Player && player.itemInHand.type == Material.BLAZE_ROD) {
            if (canUse) {
                if (!inCooldown(player)) {
                    cantDrop.add(rightClicked.uniqueId)

                    Effects.sendParticle(EnumParticle.FLAME, rightClicked.location, 1)

                    rightClicked.playSound(rightClicked.location, Sound.BLAZE_BREATH, 1.0F, 1.0F)

                    object : BukkitRunnable() {
                        override fun run() {
                            cantDrop.remove(rightClicked.uniqueId)
                        }
                    }.runTaskLaterAsynchronously(RobotoHG.INSTANCE, 100L)

                    setCooldown(player, 15)
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
    fun onPlayerItemDrop(event: PlayerDropItemEvent) {
        val player = event.player

        if (player.uniqueId in cantDrop) {
            event.isCancelled = true
            player.updateInventory()
        }
    }
}