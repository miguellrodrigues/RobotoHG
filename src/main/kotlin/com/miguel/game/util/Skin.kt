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

package com.miguel.game.util

import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.*

class Skin(val uuid: UUID) {

    lateinit var name: String
    lateinit var value: String
    lateinit var signature: String

    init {
        load()
    }

    private fun load() {
        val id = uuid.toString().replace("-", "")

        val url = "https://sessionserver.mojang.com/session/minecraft/profile/$id?unsigned=false"

        val client = OkHttpClient()
        val request: Request = Request.Builder().url(url).build()

        try {
            val responseBody = client.newCall(request).execute()

            val jsonObject = JsonParser().parse(responseBody.body?.string()).asJsonObject

            val properties = jsonObject["properties"].asJsonArray

            properties.forEach { property ->
                val propertyObject = property.asJsonObject

                name = propertyObject["name"].asString
                value = propertyObject["value"].asString
                signature = propertyObject["signature"].asString
            }

        } catch (e: IOException) {
            throw Error(e.message)
        }
    }
}