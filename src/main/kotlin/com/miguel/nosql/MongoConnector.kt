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

package com.miguel.nosql

import com.miguel.RobotoHG
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import org.bukkit.Bukkit

object MongoConnector {

    lateinit var client: MongoClient
    var success = false

    @Synchronized
    fun connect() {
        println("Inicializando conexão MongoDB")

        try {
            val uri = MongoClientURI(
                    ""
            )

            val mongoClient = MongoClient(uri)
            client = mongoClient

            println("Conexão MongoDB inicializada com sucesso")
            success = true
        } catch (e: Exception) {
            println("Erro ao inicializar conexão: ${e.message}")
            Bukkit.getPluginManager().disablePlugin(RobotoHG.INSTANCE)
        }
    }
}
