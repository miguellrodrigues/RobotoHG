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

package com.miguel.game.data

import com.miguel.nosql.MongoManager
import java.util.*
import kotlin.collections.HashMap

object PlayerData {

    private val dataMap: HashMap<UUID, Data> = HashMap()

    private val gameDataMap: HashMap<UUID, Data> = HashMap()

    @Synchronized
    fun loadAllData() {
        val allUUID = MongoManager.getAllUUID()

        if (allUUID.isNotEmpty()) {
            allUUID.forEach { uuid ->
                val data = Data()

                data.uuid = uuid

                data.kills = MongoManager.getValue(uuid, "kills") as Int
                data.deaths = MongoManager.getValue(uuid, "deaths") as Int
                data.wins = MongoManager.getValue(uuid, "wins") as Int

                data.firstPlayed = Date(MongoManager.getValue(uuid, "firstPlayed").toString().toLong())

                data.dailyKit = Date(MongoManager.getValue(uuid, "dailyKit").toString().toLong())

                dataMap[uuid] = data
            }
        }
    }

    @Synchronized
    fun createData(uuid: UUID) {
        if (uuid !in gameDataMap) {
            val data = Data()

            data.uuid = uuid

            data.kills = 0
            data.deaths = 0
            data.wins = 0

            if (uuid in dataMap) {
                val dataMap = dataMap[uuid]!!

                data.firstPlayed = dataMap.firstPlayed
                data.dailyKit = dataMap.dailyKit
            } else {
                data.firstPlayed = Date()
                data.dailyKit = Date()
            }

            gameDataMap[uuid] = data
        }
    }

    @Synchronized
    fun saveData() {
        gameDataMap.keys.forEach { uuid ->
            val gameData = gameDataMap[uuid]!!

            if (uuid in dataMap) {
                val data = dataMap[uuid]!!

                gameData.kills += data.kills
                gameData.deaths += data.deaths
                gameData.wins += data.wins
            }

            gameData.save()
        }
    }

    @Synchronized
    fun getData(uuid: UUID): Data? {
        if (uuid in gameDataMap) {
            return gameDataMap[uuid]!!
        }

        return null
    }

    @Synchronized
    fun getStoredData(uuid: UUID): Data {
        if (uuid in dataMap) {
            return dataMap[uuid]!!
        }

        return gameDataMap[uuid]!!
    }

    class Data {
        lateinit var firstPlayed: Date

        var kills = 0
        var deaths = 0
        var wins = 0

        lateinit var dailyKit: Date

        lateinit var uuid: UUID

        @Synchronized
        fun save() {
            MongoManager.createPlayer(uuid, firstPlayed.time)

            MongoManager.setValue(uuid, "kills", kills)
            MongoManager.setValue(uuid, "deaths", deaths)
            MongoManager.setValue(uuid, "wins", wins)

            MongoManager.setValue(uuid, "dailyKit", dailyKit.time)
        }

        override fun toString(): String {
            return "Data(firstPlayed=$firstPlayed, kills=$kills, deaths=$deaths, wins=$wins, uuid=$uuid)"
        }
    }
}