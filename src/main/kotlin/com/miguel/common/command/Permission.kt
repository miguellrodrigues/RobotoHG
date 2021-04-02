/*
 *
 *  * Copyright (©) RobotoHG-1.8
 *  *
 *  * Projeto desenvolvido por Miguell Rodrigues
 *  * Todos os direitos reservados
 *  *
 *  * Modificado em: 27/05/2020 15:14
 *
 */

package com.miguel.common.command

import org.bukkit.command.CommandSender

enum class Permission(val node: String) {

    NONE(""),
    TEMPO("tempo"),
    TAG("tag."),
    START("start"),
    DAMAGE("damage"),
    CHAT("chat"),
    CLEAR_CHAT("clearchat"),
    EVENT("evento"),
    FAKE("fake"),
    CONFIG("config"),
    SKIN("skin"),
    FEAST("feast"),
    MINIFEAST("minifeast"),
    ADMIN("admin");

    companion object {
        private fun getPermission(permission: Permission): String {
            return "robotohg.command." + permission.node
        }

        fun has(permission: Permission, target: CommandSender): Boolean {
            return target.hasPermission(
                    getPermission(
                            permission
                    )
            )
        }
    }
}