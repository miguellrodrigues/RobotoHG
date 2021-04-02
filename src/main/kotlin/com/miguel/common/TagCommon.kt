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

package com.miguel.common

import com.miguel.common.command.Permission
import org.bukkit.entity.Player
import java.util.*

enum class TagCommon(private var tagPrefix: String, var nameColor: String) {

    DONO("§4§lDONO", "§4"),
    DEV("§3§lDEV", "§3"),
    ADMIN("§e§lADMIN", "§e"),
    MOD("§5§lMOD", "§5"),
    YT("§c§lYT", "§c"),
    BETA("§2§lBETA", "§2"),
    VIP("§a§lVIP", "§a"),
    PLAYER("§7", "§7"),
    PRO("§6§lPRO", "§6"),
    MVP("§9§lMVP", "§9"),
    FIRSTBLOOD("§cFBLOOD", "§c");

    val formattedNametag: String
        get() = tagPrefix + if (tagPrefix != nameColor) "§r $nameColor" else ""

    val tagPriority: Int
        get() {
            val priorityMap = HashMap<TagCommon, Int>()

            val tagCommons = ArrayList<TagCommon>()

            tagCommons.addAll(values())

            tagCommons.indices.forEach { i ->
                priorityMap[tagCommons[i]] = i + 1
            }

            return if (priorityMap.containsKey(this)) {
                Integer.valueOf("-" + (priorityMap[this]!! + 1000))
            } else {
                Integer.valueOf("-" + (priorityMap.size + 1000))
            }
        }

    companion object {
        var playersTags = HashMap<UUID, TagCommon>()

        fun getTag(player: Player): TagCommon {
            if (playersTags.containsKey(player.uniqueId)) {
                val tag = playersTags[player.uniqueId]!!

                val permission = "${Permission.TAG.node}${tag.name.toLowerCase()}"

                return if (player.hasPermission(permission)) {
                    tag
                } else {
                    playersTags.remove(player.uniqueId)
                    getTag(player)
                }
            } else {
                values().forEach { tag ->
                    val permission = "${Permission.TAG.node}${tag.name.toLowerCase()}"

                    if (player.hasPermission(permission)) {
                        playersTags[player.uniqueId] = tag
                        return playersTags[player.uniqueId]!!
                    }
                }
            }

            playersTags[player.uniqueId] = PLAYER

            return playersTags[player.uniqueId]!!
        }

        fun setTag(player: Player, tagCommon: TagCommon) {
            if (playersTags.containsKey(player.uniqueId)) {
                if (tagCommon.formattedNametag != playersTags[player.uniqueId]!!.formattedNametag) {
                    playersTags[player.uniqueId] = tagCommon
                }
            } else {
                playersTags[player.uniqueId] = tagCommon
            }
        }

        fun removeTag(player: Player) {
            if (player.uniqueId in playersTags) {
                playersTags.remove(player.uniqueId)
            }
        }
    }
}