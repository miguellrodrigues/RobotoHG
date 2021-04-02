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

import com.google.gson.JsonParser
import com.miguel.RobotoHG
import org.apache.commons.io.FileUtils
import org.bukkit.Location
import org.bukkit.Material
import java.io.File

object JsonStructure {

    @Synchronized
    fun load(location: Location, fileName: String): MutableList<FutureBlock> {
        val list: MutableList<FutureBlock> = ArrayList()

        val file = File(RobotoHG.INSTANCE.dataFolder, "structures/$fileName.json")

        try {
            val jsonObject = JsonParser().parse(FileUtils.readFileToString(file)).asJsonObject

            val blocksArray = jsonObject["blocks"].asJsonArray

            blocksArray.forEach { t ->
                val obj = t.asJsonObject

                val x = obj["x"].asDouble
                val y = obj["y"].asDouble
                val z = obj["z"].asDouble

                val material = obj["material_name"].asString
                val data = obj["material_data"].asByte

                val loc = location.clone().add(x, y, z)

                val futureBlock = FutureBlock(
                        loc,
                        material,
                        data
                )

                list.add(futureBlock)
            }

        } catch (e: Exception) {
            return list
        }

        return list
    }

    class FutureBlock(val location: Location, val material_name: String, private val material_data: Byte) {

        @Synchronized
        fun place() {
            if (location.block.type != Material.AIR) location.block.type = Material.AIR

            location.block.type = Material.getMaterial(material_name)
            location.block.data = material_data
        }
    }
}