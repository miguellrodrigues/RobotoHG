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

import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import org.apache.commons.lang.Validate
import org.bson.Document
import java.util.*
import kotlin.collections.ArrayList

object MongoManager {

    private lateinit var client: MongoClient
    private lateinit var database: MongoDatabase

    private lateinit var collection: MongoCollection<Document>

    @Synchronized
    fun init() {
        MongoConnector.connect()

        client = MongoConnector.client
        database = client.getDatabase("Robotohg")

        if (!database.listCollectionNames().contains("players")) {
            database.createCollection("players")
        }

        collection = database.getCollection("players")
    }

    @Synchronized
    fun createPlayer(uuid: UUID, firstPlayed: Long) {
        if (!playerExist(uuid)) {
            val playerDocument = Document("uuid", uuid.toString())
                    .append("kills", 0)
                    .append("deaths", 0)
                    .append("wins", 0)
                    .append("firstPlayed", firstPlayed)
                    .append("dailyKit", 0L)

            collection.insertOne(playerDocument)
        }
    }

    @Synchronized
    fun setValue(uuid: UUID, column: String, value: Any) {
        Validate.notNull(uuid, "uuid invalido")

        if (playerExist(uuid)) {
            collection.findOneAndUpdate(
                    Filters.eq("uuid", uuid.toString()),
                    Updates.set(column, value)
            )
        }
    }

    @Synchronized
    fun getValue(uuid: UUID, column: String): Any? {
        Validate.notNull(uuid, "uuid invalido")

        if (playerExist(uuid)) {
            val find = collection.find(Filters.eq("uuid", uuid.toString())).first()!!

            return find[column]
        }

        return null
    }

    @Synchronized
    fun getAllUUID(): List<UUID> {
        val list: MutableList<UUID> = ArrayList()

        val iterable = collection.find()

        val iterator = iterable.iterator()

        while (iterator.hasNext()) {
            val next = iterator.next()
            list.add(UUID.fromString(next["uuid"] as String))
        }

        return list
    }

    @Synchronized
    private fun playerExist(uuid: UUID): Boolean {
        val find = collection.find(Filters.eq("uuid", uuid.toString())).limit(1)

        if (find.count() == 1) {
            return true
        }

        return false
    }
}