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

import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.miguel.RobotoHG
import org.apache.commons.io.FileUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


object Manager {

    private val INSTANCE = RobotoHG.INSTANCE

    private val biomeFile = File(RobotoHG.INSTANCE.dataFolder, "biomes.json")

    private lateinit var biomeArray: JsonArray

    @Synchronized
    fun formatTime(i: Int): String {
        val a: Int = i / 60
        val b: Int = i % 60

        val d: String = if (b >= 10) "" + b else "0$b"

        return String.format("%s %s", "${a}m", "${d}s")
    }

    @Synchronized
    fun copyFiles(files: Array<String>) {
        val defaultDirectory = "structures/"

        files.forEach { file ->
            FileUtils.copyInputStreamToFile(
                    INSTANCE.getResource("$defaultDirectory$file"),
                    File(INSTANCE.dataFolder, "$defaultDirectory$file")
            )
        }

        biomeArray = JsonParser().parse(FileUtils.readFileToString(biomeFile)).asJsonArray
    }

    @Synchronized
    fun getRandomLocation(): Location {
        val x = (-450..450).random()
        val z = (-450..450).random()

        val world = Bukkit.getWorld("world")

        return Location(
                world,
                x.toDouble(),
                world.getHighestBlockYAt(x, z).toDouble(),
                z.toDouble()
        )
    }

    @Synchronized
    fun removeBlocksBellow(block: Block, height: Int) {
        val loc = block.location
        var block1 = loc.block
        do {
            block1.type = Material.AIR
            loc.y = loc.y + 1
            block1 = loc.block
        } while (loc.y <= height)
    }

    fun getCenter(loc: Location): Location {
        return Location(
                loc.world,
                getRelativeCord(loc.blockX),
                getRelativeCord(loc.blockY),
                getRelativeCord(loc.blockZ)
        )
    }

    @Synchronized
    fun sphere(location: Location, radius: Int): MutableList<Location> {
        val list: MutableList<Location> = ArrayList()
        for (i in 0 until radius) {
            for (x in -radius..radius) {
                for (z in -radius..radius) {
                    val loc = Location(
                            location.world,
                            location.x + (x - i / 2),
                            location.y - i,
                            location.z + (z - i / 2)
                    )

                    if (loc.distance(location) < radius) {
                        list.add(loc)
                    }
                }
            }
        }
        return list
    }

    private fun getRelativeCord(i: Int): Double {
        var d: Double = i.toDouble()

        d = if (d < 0) d - .5 else d + .5

        return d
    }

    @Synchronized
    fun delFile(file: File) {
        if (file.exists()) {
            if (file.isDirectory) {
                for (listFile in file.listFiles()) {
                    delFile(listFile)
                }
            }

            file.delete()
        }
    }

    @Synchronized
    fun plainSphere(location: Location, radius: Int) {
        for (x in -radius..radius) {
            for (z in -radius..radius) {
                val loc = Location(location.world, location.x + x, location.y, location.z + z)

                if (location.distance(loc) <= radius) {
                    loc.block.type = Material.GRASS
                    removeBlocksBellow(loc.block, 50)
                }
            }
        }
    }

    @Synchronized
    fun clearArea(location: Location, radius: Int) {
        val x1 = location.blockX
        val y1 = location.blockY
        val z1 = location.blockZ

        for (x2 in x1 - radius..x1 + radius) {
            for (z2 in z1 - radius..z1 + radius) {
                for (y2 in y1 + 1..y1 + radius) {
                    if ((x1 - x2) * (x1 - x2) + (z1 - z2) * (z1 - z2) <= radius * radius) {
                        location.world.getBlockAt(x2, y2, z2).type = Material.AIR
                    }
                }
            }
        }
    }

    @Synchronized
    fun getBiomeName(block: Block): String {
        val biome = block.biome

        lateinit var name: String

        biomeArray.forEach {
            val biomeObject = it.asJsonObject

            val biomeName = biomeObject["name"].asString

            if (biomeName.toLowerCase() == biome.toString().toLowerCase()) {
                name = biomeObject["prefix"].asString
                return@forEach
            }
        }

        return name
    }

    @Synchronized
    fun getBiomeMaterial(block: Block): Material {
        val biome = block.biome

        lateinit var name: String

        biomeArray.forEach {
            val biomeObject = it.asJsonObject

            val biomeName = biomeObject["name"].asString

            if (biomeName.toLowerCase() == biome.toString().toLowerCase()) {
                name = biomeObject["material"].asString
                return@forEach
            }
        }

        return Material.getMaterial(name)
    }

    @Synchronized
    fun sendToServer(p: Player, server: String) {
        val b: ByteArrayDataOutput = ByteStreams.newDataOutput()
        b.writeUTF("Connect")
        b.writeUTF(server)
        p.sendPluginMessage(RobotoHG.INSTANCE, "BungeeCord", b.toByteArray())
    }

    private fun getConnectedBlocks(
            block: Block,
            results: MutableSet<Block>,
            todo: MutableList<Block>,
            material: Material
    ) {
        BlockFace.values().forEach { blockFace ->
            val relative = block.getRelative(blockFace)

            if (relative.type == material) {
                if (results.add(relative)) {
                    todo.add(relative)
                }
            }
        }
    }

    fun getConnectedBlocks(block: Block, material: Material): Set<Block> {
        val set: MutableSet<Block> = HashSet()

        val list = LinkedList<Block>()

        list.add(block)

        var poll = list.poll()

        while (poll != null) {
            getConnectedBlocks(poll, set, list, material)
            poll = list.poll()
        }

        return set
    }
}