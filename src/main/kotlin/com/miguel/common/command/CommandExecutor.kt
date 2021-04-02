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

import com.miguel.common.command.Permission.Companion.has
import com.miguel.game.GameStage
import com.miguel.game.Timer
import org.apache.commons.lang.ArrayUtils
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

class CommandExecutor : BukkitCommand("hg") {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage(CommandManager.error.toString() + "Comando não encontrado!")
            return true
        }

        if (CommandManager.getCommand(args[0]) == null) {
            sender.sendMessage(CommandManager.error.toString() + "Comando não encontrado!")
            return true
        }

        val command = CommandManager.getCommand(args[0])!!
        val commandArgs = ArrayUtils.remove(args, 0)

        if (sender is Player && !command.player) {
            sender.sendMessage(CommandManager.error.toString() + "Este comando não pode ser executado por um player!")
            return true
        }

        if (sender is ConsoleCommandSender && !command.console) {
            sender.sendMessage(CommandManager.error.toString() + "Este comando não pode ser executado via console!")
            return true
        }

        if (command.permission != Permission.NONE && !has(command.permission, sender)) {
            sender.sendMessage(CommandManager.error.toString() + "Você não tem permissão para usar este comando!")
            return true
        }

        if (command.gameStage.size == 1) {
            if (command.gameStage[0] != GameStage.NONE && command.gameStage[0] != Timer.gameStage) {
                sender.sendMessage(CommandManager.error.toString() + "Este comando não pode ser executado agora!")
                return true
            }
        } else {
            if (Timer.gameStage !in command.gameStage) {
                sender.sendMessage(CommandManager.error.toString() + "Este comando não pode ser executado agora!")
                return true
            }
        }

        if (commandArgs.size < command.min || commandArgs.size > command.max && command.max != -1) {
            sender.sendMessage(
                    CommandManager.error
                            .toString() + command.usage
            )

            return true
        }

        CommandManager.execute(command, sender, commandArgs)

        return false
    }

    /*override fun tabComplete(sender: CommandSender, alias: String, args: Array<String>): List<String> {
        return super.tabComplete(sender, alias, args)
    }*/
}