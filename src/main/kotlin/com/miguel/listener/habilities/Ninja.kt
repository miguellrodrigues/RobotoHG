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
import com.miguel.util.Strings
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

class Ninja : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        return arrayListOf(ItemStack(Material.AIR))
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eNinja",
                arrayOf(" ", "§fTeleporte-se até seu inimigo", " ", "§eClique para selecionar"),
                Material.IRON_BOOTS
        )
    }

    override fun getPrice(): Int {
        return 0
    }

    override val cooldownMap: HashMap<UUID, Long> = HashMap()

    private val lastHit: HashMap<UUID, Player> = HashMap()

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        val entity = event.entity

        if (entity is Player && damager is Player) {
            if (using(damager)) {
                lastHit[damager.uniqueId] = entity
            }
        }
    }

    @EventHandler
    fun onPlayerSneak(event: PlayerToggleSneakEvent) {
        val player = event.player

        if (using(player)) {
            if (!inCooldown(player)) {
                if (lastHit.containsKey(player.uniqueId)) {
                    if (canUse) {
                        val lastHitted = lastHit[player.uniqueId]!!

                        if (lastHitted.location.distance(player.location) <= 35) {
                            player.teleport(lastHitted.location)
                            setCooldown(player, 15)
                        } else {
                            player.sendMessage("${Strings.PREFIX} §fJogador muito longe§f!")
                        }
                    } else {

                        player.sendMessage(
                                "${Strings.PREFIX} §fVocê não pode usar seu kit agora§e!"
                        )

                    }


                }
            } else {
                player.sendMessage(
                        "${Strings.PREFIX} §fAguarde §f${getCooldown(player)} §fPara usar novamente§e!"
                )
            }

        }
    }
}