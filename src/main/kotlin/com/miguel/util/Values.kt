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

package com.miguel.util

import org.bukkit.Material

object Values {

    const val MIN_PLAYERS = 8

    var OLD_FEAST = false

    const val START_FEAST = 560

    const val SPAWN_FEAST = 341

    const val MINI_FEAST_INTERVAL = 300

    var EVENT_MODE = false

    var GLOBAL_DAMAGE = true

    var GLOBAL_CHAT = true

    var FEAST_SPAWN = false

    var firstBlood = false

    val feastItems = listOf(
            Material.DIAMOND_SWORD,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS,
            Material.DIAMOND_HELMET,
            Material.ENDER_PEARL,
            Material.EXP_BOTTLE,
            Material.TNT
    )

    val miniFeastItems = listOf(
            Material.IRON_INGOT,
            Material.STICK,
            Material.EXP_BOTTLE,
            Material.ENDER_PEARL,
            Material.GOLDEN_APPLE
    )

    val graceItem = listOf(
            Material.IRON_SWORD,
            Material.STICK,
            Material.DIAMOND,
            Material.ENDER_PEARL,
            Material.GOLDEN_APPLE
    )
}