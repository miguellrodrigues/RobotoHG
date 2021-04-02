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
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

class None : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        return arrayListOf(ItemStack(Material.AIR))
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eNone",
                arrayOf(" ", "§fSem habilidade", " ", "§eClique para selecionar"),
                Material.STICK
        )
    }

    override fun getPrice(): Int {
        return 0
    }

    override val cooldownMap: HashMap<UUID, Long> = HashMap()
}