/*
 *
 *  * Copyright (©) RobotoHG-1.8
 *  *
 *  * Projeto desenvolvido por Miguell Rodrigues
 *  * Todos os direitos reservados
 *  *
 *  * Modificado em: 08/06/2020 18:47
 *
 */

package com.miguel

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.miguel.commands.GameCommands
import com.miguel.commands.GmCommands
import com.miguel.common.command.CommandExecutor
import com.miguel.common.command.CommandManager
import com.miguel.game.data.PlayerData
import com.miguel.game.kit.KitManager
import com.miguel.game.manager.*
import com.miguel.listener.*
import com.miguel.nosql.MongoConnector
import com.miguel.nosql.MongoManager
import com.miguel.packet.CustomPing
import com.miguel.structures.StructureManager
import com.miguel.util.Structures
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.io.FileUtils
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.CraftServer
import org.bukkit.inventory.Recipe
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.collections.ArrayList

class RobotoHG : JavaPlugin() {

    companion object {
        lateinit var INSTANCE: RobotoHG
    }

    init {
        val mongoLogger = Logger.getLogger("org.mongodb.driver")
        mongoLogger.level = Level.SEVERE
    }

    override fun onLoad() {
        INSTANCE = this

        //Manager.delFile(File("./world"))

        if (!File(dataFolder, "structures").exists())
            File(dataFolder, "structures").mkdir()

        FileUtils.copyInputStreamToFile(
                getResource("biomes.json"),
                File(dataFolder, "biomes.json")
        )

        Manager.copyFiles(
                arrayOf(
                        "coliseum.json",
                        "feast.json",
                        "mini-feast.json",
                        "cake.json"
                )
        )

        MongoManager.init()

        PlayerData.loadAllData()

        val iterator = server.recipeIterator().iterator()

        val lst: MutableList<Recipe> = ArrayList()

        iterator.forEach { recipe ->
            if (recipe.result.type != Material.MUSHROOM_SOUP) {
                lst.add(recipe)
            }
        }

        server.clearRecipes()

        lst.forEach { recipe ->
            server.addRecipe(recipe)
        }

        config.options().copyDefaults(true)
        config.options().copyHeader(true)

        saveDefaultConfig()
    }

    override fun onEnable() {
        val licenseManager = LicenseManager(this)
        val plugin = server.pluginManager.getPlugin("SimplePermissions")

        if (!licenseManager.verify() || plugin == null || !MongoConnector.success) {
            server.pluginManager.disablePlugin(this)
            return
        }

        server.getWorld("world").setSpawnLocation(0, 200, 0)

        StructureManager.add(
                arrayOf(
                        "coliseum",
                        "feast",
                        "mini-feast",
                        "cake"
                )
        )

        Structures.load()

        TagManager()

        server.pluginManager.registerEvents(PlayerEvents(), this)
        server.pluginManager.registerEvents(GameEvents(), this)
        server.pluginManager.registerEvents(EntityEvents(), this)
        server.pluginManager.registerEvents(ServerEvents(), this)
        server.pluginManager.registerEvents(InventoryEvents(), this)

        KitManager.registerKits()

        (server as CraftServer).commandMap.register(
                "hg",
                CommandExecutor()
        )

        CommandManager.register(GameCommands::class.java)
        CommandManager.register(GmCommands::class.java)

        CustomPing(
                this,
                arrayOf(
                        "",
                        "§a§k.§r §fVenha jogar conosco §e! §a§k.§r",
                        "",
                        "§fBy §bAccess_Token"
                )
        )

        GameManager.worldBorder()
        GameManager.addCustomRecipes()

        FeastManager.init()
        MiniFeastManager.init()

        server.getWorld("world").entities.forEach {
            it.remove()
        }
    }

    override fun onDisable() {
        PlayerData.saveData()

        saveConfig()
    }

    private class LicenseManager(plugin: JavaPlugin) {

        private val id = plugin.config.getString("license.id") ?: ""
        private val name = plugin.config.getString("license.name") ?: ""
        private val key = plugin.config.getString("license.key") ?: ""

        @Synchronized
        fun verify(): Boolean {
            lateinit var ip: String

            DatagramSocket().use { socket ->
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002)
                ip = socket.localAddress.hostAddress
                socket.close()
            }

            val storedLicense = License(id, ip, name, key)

            return try {
                val serverLicense = getLicense(
                        "${
                            String(
                                    Base64.getDecoder()
                                            .decode("")
                            )
                        }$id"
                )

                storedLicense == serverLicense
            } catch (e: Exception) {
                false
            }
        }

        @Synchronized
        fun getLicense(url: String): License {
            val client = OkHttpClient()
            val request: Request = Request.Builder().url(url).build()

            lateinit var license: License

            return try {
                val response = client.newCall(request).execute()

                license = Gson().fromJson(
                        JsonParser().parse(response.body?.string()).asJsonObject,
                        License::class.java
                )

                license
            } catch (e: Exception) {
                license
            }
        }

        private class License(val _id: String, val ip: String, val name: String, val key: String) {

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as License

                if (_id != other._id) return false
                //if (ip != other.ip) return false
                if (name != other.name) return false
                if (key != other.key) return false

                return true
            }

            override fun toString(): String {
                return "License(id='$_id', ip='$ip', name='$name', key='$key')"
            }
        }
    }
}
