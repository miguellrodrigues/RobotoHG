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
import com.miguel.game.kit.KitManager
import com.miguel.game.manager.GameManager
import com.miguel.util.Strings
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

class CopyCat : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        return arrayListOf(ItemStack(Material.AIR))
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eCopyCat",
                arrayOf(" ", "§fReceba a habilidade do seu inimigo ao mata-lo", " ", "§eClique para selecionar"),
                Material.YELLOW_FLOWER
        )
    }

    override fun getPrice(): Int {
        return 0
    }

    override val cooldownMap: HashMap<UUID, Long> = HashMap()

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val entity = event.entity

        val killer = entity.killer

        if (killer != null && using(killer)) {
            val kits = KitManager.getKits(entity)

            val kit = kits[(kits.indices).random()]

            KitManager.setKit(killer, kit, 1)

            KitManager.giveItems(killer)

            killer.sendMessage("${Strings.PREFIX} §fVocê recebeu o kit §f${kit.getName()}")
        }
    }
}