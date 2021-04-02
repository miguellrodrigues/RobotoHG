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

package com.miguel.packet.hologram

import org.bukkit.Location
import org.bukkit.entity.Player

object HologramManager {

    private val holograms: MutableList<Hologram> = ArrayList()

    private val mutableHolograms: HashMap<String, Hologram> = HashMap()

    private fun createHologram(location: Location): Hologram {
        val hologram = Hologram()

        hologram.location = location

        holograms.add(hologram)

        return hologram
    }

    fun createHologram(location: Location, lines: Array<String>, player: Player): Hologram {
        val createHologram = this.createHologram(location)

        createHologram.player = player
        createHologram.lines = lines

        createHologram.show()

        return createHologram
    }

    fun createMutableHologram(name: String, location: Location): Hologram? {
        if (!mutableHolograms.containsKey(name)) {
            val hologram = Hologram()

            hologram.location = location

            mutableHolograms[name] = hologram

            return hologram
        }

        return null
    }

    fun getHologram(player: Player): Hologram? {
        holograms.forEach { hologram ->
            if (hologram.player == player) {
                return hologram
            }
        }

        return null
    }

    fun getMutableHologram(name: String): Hologram? {
        if (mutableHolograms.containsKey(name)) {
            return mutableHolograms[name]!!
        }

        return null
    }

    fun destroyAll() {
        if (holograms.isNotEmpty()) {
            holograms.forEach { hologram ->
                hologram.destroy()
            }
        }
    }
}