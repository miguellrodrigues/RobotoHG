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

package com.miguel.game.manager

import com.miguel.util.Strings
import com.miguel.util.Structures
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Chest

object MiniFeastManager {

    private val miniFeast = Structures.miniFeast

    var location = Location(
            Bukkit.getWorld("world"),
            .0,
            .0,
            .0
    )

    fun init() {
        miniFeast.load()
    }

    @Synchronized
    fun launch() {
        location = Manager.getRandomLocation()

        miniFeast.placeLoaded(location)

        miniFeast.locations.forEach { block ->
            if (block.type == Material.CHEST) {
                GameManager.fillChest(block.state as Chest, "minifeast")
            }
        }

        GameManager.sendCustomMessage(
                "${Strings.PREFIX} §fUm mini feast spawnow em §a(§f" +
                        "${location.blockX}§f, " +
                        "§f${location.blockY}§f, " +
                        "§f${location.blockZ}§a)",
                "minifeast",
                "§fClique para apontar para o último §aMiniFeast §f!"
        )

        miniFeast.locations.clear()
        miniFeast.blocks.clear()
    }
}