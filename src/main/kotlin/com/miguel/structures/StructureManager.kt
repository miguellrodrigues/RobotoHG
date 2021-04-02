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

package com.miguel.structures

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block

object StructureManager {

    private val loaded: HashMap<String, Structure> = HashMap()

    fun add(name: String) {
        if (!loaded.containsKey(name)) {
            val struct = Structure(name)

            loaded[name] = struct
        }
    }

    fun add(structures: Array<String>) {
        structures.forEach { str ->
            add(str)
        }
    }

    fun get(name: String): Structure {
        if (!loaded.containsKey(name))
            add(name)

        return loaded[name]!!
    }

    class Structure(private val name: String) {
        val locations: MutableList<Block> = ArrayList()

        lateinit var blocks: MutableList<JsonStructure.FutureBlock>

        @Synchronized
        fun place(location: Location) {
            blocks = JsonStructure.load(location, name)

            blocks.forEach { fb ->
                fb.place()
                locations.add(fb.location.block)
            }
        }

        @Synchronized
        fun placeLoaded(location: Location) {
            blocks.forEach { fb ->
                fb.location.add(location)
                fb.place()
                locations.add(fb.location.block)
            }
        }

        @Synchronized
        fun load() {
            val location = Location(
                    Bukkit.getWorld("world"),
                    0.0,
                    0.0,
                    0.0
            )

            blocks = JsonStructure.load(location, name)

            blocks.forEach { fb ->
                locations.add(fb.location.clone().block)
            }
        }

        @Synchronized
        fun load(location: Location) {
            blocks = JsonStructure.load(location, name)

            blocks.forEach { fb ->
                locations.add(fb.location.clone().block)
            }
        }

        @Synchronized
        fun destroy(remove: Boolean) {
            if (remove) {
                locations.forEach { block ->
                    block.type = Material.AIR
                }
            }

            locations.clear()
            blocks.clear()
        }
    }
}