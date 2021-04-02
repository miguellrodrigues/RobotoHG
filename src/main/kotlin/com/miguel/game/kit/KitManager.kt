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

package com.miguel.game.kit

import com.miguel.RobotoHG
import com.miguel.game.manager.GameManager
import com.miguel.game.rplayer.RobotoPlayerManager
import com.miguel.listener.habilities.*
import com.miguel.util.Permissions
import org.bukkit.Material
import org.bukkit.entity.Player

object KitManager {

    private val INSTANCE = RobotoHG.INSTANCE

    private val kits: MutableList<Kit> = ArrayList()

    fun registerKits() {
        kits.add(Acchiles())
        kits.add(Ajnin())
        kits.add(Anchor())
        kits.add(AntiStomper())
        kits.add(Boxer())
        kits.add(C4())
        kits.add(CheckPoint())
        kits.add(CopyCat())
        kits.add(EnderMage())
        kits.add(Ninja())
        kits.add(None())
        kits.add(Stomper())
        kits.add(Switcher())
        kits.add(BomberMan())
        kits.add(Digger())
        kits.add(Fireman())
        kits.add(Kangaroo())
        kits.add(MissileMan())
        kits.add(Fisherman())
        kits.add(Grappler())
        kits.add(Gladiator())
        kits.add(Launcher())
        kits.add(Miner())
        kits.add(Viper())
        kits.add(Snail())
        kits.add(Hulk())
        kits.add(Undropper())
        kits.add(Worm())
        kits.add(Viking())
        kits.add(Specialist())
        kits.add(GravityGun())

        kits.forEach { kit ->
            INSTANCE.server.pluginManager.registerEvents(kit, INSTANCE)
        }

        kits.sortBy { kit -> kit.getName() }
    }

    fun giveItems(player: Player) {
        val robotoPlayer = RobotoPlayerManager.getrobotoPlayer(player)

        player.inventory.addItem(GameManager.createItem("§fBússola", arrayOf(""), Material.COMPASS))

        if (robotoPlayer.primaryKit.hasItems()) {
            robotoPlayer.primaryKit.getItems().forEach { item ->
                player.inventory.addItem(item)
            }
        }

        if (robotoPlayer.secondaryKit.hasItems()) {
            robotoPlayer.secondaryKit.getItems().forEach { item ->
                player.inventory.addItem(item)
            }
        }

        player.updateInventory()
    }

    fun setKit(player: Player, kit: Kit, position: Int) {
        val robotoPlayer = RobotoPlayerManager.getrobotoPlayer(player)

        if (position == 0) {
            robotoPlayer.primaryKit = kit
        } else if (position == 1) {
            robotoPlayer.secondaryKit = kit
        }
    }

    fun getByName(name: String): Kit? {
        var kit: Kit? = null

        kits.forEach { kt ->
            if (kt.getName().toLowerCase() == name)
                kit = kt
        }

        return kit
    }

    fun getKits(player: Player): List<Kit> {
        val robotoPlayer = RobotoPlayerManager.getrobotoPlayer(player)

        return listOf(robotoPlayer.primaryKit, robotoPlayer.secondaryKit)
    }

    fun getKitsName(player: Player): String {
        val robotoPlayer = RobotoPlayerManager.getrobotoPlayer(player)

        return "§e(§f${robotoPlayer.primaryKit.getName()}§e, §f${robotoPlayer.secondaryKit.getName()}§e)"
    }

    fun getPlayerKits(player: Player, position: Int): List<Kit> {
        val playerKits: MutableList<Kit> = ArrayList()

        kits.forEach { kit ->
            val permission: String = if (position == 0) {
                Permissions.KIT
            } else {
                Permissions.SECONDARY_KIT
            }

            if (player.hasPermission("$permission${kit.getName()}"))
                playerKits.add(kit)
        }

        return playerKits
    }

    fun getNonPlayerKits(player: Player, position: Int): List<Kit> {
        val playerKits: MutableList<Kit> = ArrayList()

        kits.forEach { kit ->
            val permission: String = if (position == 0) {
                Permissions.KIT
            } else {
                Permissions.SECONDARY_KIT
            }

            if (!player.hasPermission("$permission${kit.getName()}"))
                playerKits.add(kit)
        }

        return playerKits
    }
}