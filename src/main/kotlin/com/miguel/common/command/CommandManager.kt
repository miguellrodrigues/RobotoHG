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

package com.miguel.common.command

import org.bukkit.ChatColor
import java.lang.reflect.Method
import java.util.*

object CommandManager {

    val light = ChatColor.GREEN
    val dark = ChatColor.DARK_GREEN
    val neutral = ChatColor.WHITE
    val highlight = ChatColor.AQUA
    val extra = ChatColor.DARK_RED
    val error = ChatColor.RED
    val warning = ChatColor.YELLOW

    private val commands = LinkedHashMap<Command, Method>()

    fun register(clazz: Class<*>) {
        val methods = clazz.methods
        for (method in methods) {
            if (method.isAnnotationPresent(Command::class.java)) {
                commands[method.getAnnotation(
                        Command::class.java
                )] = method


            }
        }
    }

    fun unregister(clazz: Class<*>) {
        val methods = clazz.methods
        for (method in methods) {
            if (method.isAnnotationPresent(Command::class.java)) {
                commands.remove(
                        method.getAnnotation(
                                Command::class.java
                        )
                )
            }
        }
    }

    fun unregisterAll() {
        commands.clear()
    }

    fun getCommands(): LinkedList<Command> {
        val commands = LinkedList<Command>()
        commands.addAll(CommandManager.commands.keys)
        return commands
    }

    fun getCommand(label: String): Command? {
        for (command in commands.keys) {
            for (alias in command.aliases) {
                if (label.equals(alias, ignoreCase = true)) {
                    return command
                }
            }
        }
        return null
    }

    fun execute(command: Command, vararg args: Any?) {
        try {
            commands[command]!!.invoke(commands[command]!!.declaringClass.newInstance(), *args)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}