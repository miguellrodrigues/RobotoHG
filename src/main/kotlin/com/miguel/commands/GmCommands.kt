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

package com.miguel.commands

import com.miguel.RobotoHG
import com.miguel.common.command.Command
import com.miguel.common.command.Permission
import com.miguel.game.GameStage
import com.miguel.game.manager.FeastManager
import com.miguel.game.manager.GameManager
import com.miguel.game.manager.InventoryManager
import com.miguel.game.manager.MiniFeastManager
import com.miguel.game.rplayer.RobotoPlayerManager
import com.miguel.util.Strings
import com.miguel.util.Values
import org.bukkit.Effect
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class GmCommands {

    @Command(
            aliases = ["damage", "dano", "dm", "dn"],
            description = "Ativa ou desativa o dano global",
            permission = Permission.DAMAGE,
            usage = "/damage [on/off]",
            min = 0,
            max = 1
    )
    fun onDamageCommand(sender: CommandSender, strings: Array<String?>) {
        if (strings.isNotEmpty()) {
            when (strings[0]!!.toLowerCase()) {
                "on" -> {
                    if (!Values.GLOBAL_DAMAGE) {
                        Values.GLOBAL_DAMAGE = true
                        GameManager.sendMessage("${Strings.PREFIX} §fDano global §aAtivado §f!")
                        GameManager.playSound(Sound.CLICK)
                    }
                }
                "off" -> {
                    if (Values.GLOBAL_DAMAGE) {
                        Values.GLOBAL_DAMAGE = false
                        GameManager.sendMessage("${Strings.PREFIX} §fDano global §cDesativado §f!")
                        GameManager.playSound(Sound.CLICK)
                    }
                }
                else -> {
                    sender.sendMessage("§cUse /damage [on/off]")
                }
            }
        } else {
            Values.GLOBAL_DAMAGE = !Values.GLOBAL_DAMAGE

            GameManager.sendMessage("${Strings.PREFIX} §fDano global §${if (Values.GLOBAL_DAMAGE) "aAtivado" else "cDesativado"} §f!")
            GameManager.playSound(Sound.CLICK)
        }
    }

    @Command(
            aliases = ["chat", "ch"],
            description = "Ativa ou desativa o dano global",
            permission = Permission.CHAT,
            usage = "/chat [on/off]",
            min = 0,
            max = 1
    )
    fun onChatCommand(sender: CommandSender, strings: Array<String?>) {
        if (strings.isNotEmpty()) {
            when (strings[0]!!.toLowerCase()) {
                "on" -> {
                    if (!Values.GLOBAL_CHAT) {
                        Values.GLOBAL_CHAT = true
                        GameManager.sendMessage("${Strings.PREFIX} §fChat global §aAtivado §f!")
                        GameManager.playSound(Sound.CLICK)
                    }
                }
                "off" -> {
                    if (Values.GLOBAL_CHAT) {
                        Values.GLOBAL_CHAT = false
                        GameManager.sendMessage("${Strings.PREFIX} §fChat global §cDesativado §f!")
                        GameManager.playSound(Sound.CLICK)
                    }
                }
                else -> {
                    sender.sendMessage("§cUse /chat [on/off]")
                }
            }
        } else {
            Values.GLOBAL_CHAT = !Values.GLOBAL_CHAT

            GameManager.sendMessage("${Strings.PREFIX} §fChat global §${if (Values.GLOBAL_CHAT) "aAtivado" else "cDesativado"} §f!")
            GameManager.playSound(Sound.CLICK)
        }
    }

    @Command(
            aliases = ["skit"],
            description = "Seta um kit",
            permission = Permission.DAMAGE,
            gameStage = [GameStage.INVINCIBLE, GameStage.GAME],
            usage = "/skit"
    )
    fun onSkitCommand(sender: CommandSender, strings: Array<String?>) {
        if (sender is Player) {
            val contents = sender.inventory.contents
            val armorContents = sender.inventory.armorContents

            RobotoPlayerManager.getPlaying().forEach { oplayer ->
                oplayer.player.inventory.contents = contents
                oplayer.player.inventory.armorContents = armorContents

                oplayer.player.itemInHand = sender.inventory.itemInHand
            }

            GameManager.sendMessage("${Strings.PREFIX} §fO Jogadof §a${sender.name} §fSetou um §ekit §f!")
            GameManager.playSound(Sound.NOTE_PIANO)
        }
    }

    @Command(
            aliases = ["clearchat", "cc", "c"],
            description = "Limpa o chat",
            permission = Permission.CLEAR_CHAT,
            usage = "/cc"
    )
    fun onClearChatCommand(sender: CommandSender, strings: Array<String?>) {
        if (sender is Player) {
            GameManager.players.forEach { player ->
                for (i in 0..100) {
                    player.sendMessage(" ")
                }
            }
        }

        GameManager.sendMessage("${Strings.PREFIX} §fO chat foi limpo §f!")
    }

    @Command(
            aliases = ["event", "evento", "ev"],
            description = "Ativa ou desativo o modo evento",
            permission = Permission.EVENT,
            gameStage = [GameStage.PREGAME, GameStage.INVINCIBLE],
            player = true,
            usage = "/evento [on/off]"
    )
    fun onEventCommand(sender: CommandSender, strings: Array<String?>) {
        if (strings.isNotEmpty()) {
            when (strings[0]!!.toLowerCase()) {
                "on" -> {
                    if (!Values.EVENT_MODE) {
                        Values.EVENT_MODE = true
                        GameManager.sendMessage("${Strings.PREFIX} §fModo evento §aAtivado §f!")
                        GameManager.playSound(Sound.CLICK)
                    }
                }
                "off" -> {
                    if (Values.EVENT_MODE) {
                        Values.EVENT_MODE = false
                        GameManager.sendMessage("${Strings.PREFIX} §fModo evento §cDesativado §f!")
                        GameManager.playSound(Sound.CLICK)
                    }
                }
                else -> {
                    sender.sendMessage("§cUse /evento [on/off]")
                }
            }
        } else {
            Values.EVENT_MODE = !Values.EVENT_MODE

            GameManager.sendMessage("${Strings.PREFIX} §fModo evento §${if (Values.EVENT_MODE) "aAtivado" else "cDesativado"} §f!")
            GameManager.playSound(Sound.CLICK)
        }
    }

    @Command(
            aliases = ["config", "cf"],
            description = "Abre o menu de configurações",
            permission = Permission.CONFIG,
            player = true,
            usage = "/config"
    )
    fun onConfigCommand(sender: CommandSender, strings: Array<String?>) {
        InventoryManager.createInventory(sender as Player, "config")
    }

    @Command(
            aliases = ["gates", "gt"],
            description = "Abre os portões do coliseum",
            permission = Permission.CLEAR_CHAT,
            gameStage = [GameStage.PREGAME],
            usage = "/gate"
    )
    fun onGateCommand(sender: CommandSender, strings: Array<String?>) {
        val blocks: MutableList<Block> = ArrayList()

        GameManager.coliseum.locations.forEach { block ->
            if (block.type.name.contains("PISTON")) {
                blocks.add(block)
            }
        }

        blocks.sortWith(Comparator.comparingDouble { value -> value.location.y })
        blocks.sortWith(Comparator.comparingDouble { value -> value.world.spawnLocation.distance(value.location) })

        object : BukkitRunnable() {
            var x = -1
            override fun run() {
                if (x != blocks.size - 1) {
                    x++

                    val block = blocks[x]

                    block.type = Material.AIR

                    block.world.playEffect(block.location, Effect.STEP_SOUND, block.type)
                } else {
                    blocks.clear()
                    cancel()
                }
            }
        }.runTaskTimer(RobotoHG.INSTANCE, 0L, 0L)
    }

    @Command(
            aliases = ["ffeast", "ff"],
            description = "Força o feast",
            permission = Permission.FEAST,
            player = true,
            gameStage = [GameStage.GAME],
            usage = "/feast"
    )
    fun onFeastCommand(sender: CommandSender, strings: Array<String?>) {
        if (Values.FEAST_SPAWN) {
            sender.sendMessage("§cO feast já foi lançado §e!")
        } else {
            FeastManager.launch()
        }
    }

    @Command(
            aliases = ["fminifeast", "mf"],
            description = "Lança um mini-feast",
            permission = Permission.MINIFEAST,
            player = true,
            gameStage = [GameStage.GAME],
            usage = "/feast"
    )
    fun onMiniFeastCommand(sender: CommandSender, strings: Array<String?>) {
        MiniFeastManager.launch()
    }

    @Command(
            aliases = ["admin"],
            description = "Entra no modo admin",
            permission = Permission.ADMIN,
            player = true,
            usage = "/admin"
    )
    fun onAdminCommand(sender: CommandSender, strings: Array<String?>) {
        val robotoPlayer = RobotoPlayerManager.getrobotoPlayer(sender as Player)
        robotoPlayer.admin()
    }
}