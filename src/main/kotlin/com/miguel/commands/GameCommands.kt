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
import com.miguel.common.TagCommon
import com.miguel.common.command.Command
import com.miguel.common.command.Permission
import com.miguel.game.GameStage
import com.miguel.game.Timer
import com.miguel.game.data.PlayerData
import com.miguel.game.manager.*
import com.miguel.game.rplayer.RobotoPlayerManager
import com.miguel.packet.hologram.HologramManager
import com.miguel.util.Strings
import com.miguel.util.Values
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.apache.commons.lang3.Range
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.stream.Collectors

class GameCommands {

    @Command(
            aliases = ["tempo", "tm"],
            description = "Altera o tempo da partida",
            permission = Permission.TEMPO,
            usage = "/tempo [tempo]",
            min = 1,
            max = 1
    )
    fun timeCommand(sender: CommandSender, strings: Array<String>) {
        val s = strings[0]

        val time: Int

        try {
            time = s.toInt()
        } catch (e: Exception) {
            sender.sendMessage("§cUtilize apenas números§e!")
            return
        }

        val gameTime = Timer.gameTime

        if (time <= 0 || (time < gameTime && Timer.gameStage == GameStage.GAME)) {
            sender.sendMessage("§cValor inválido§e!")
            return
        }

        Timer.gameTime = time
    }

    @Command(
            aliases = ["tag", "tg"],
            description = "Selecione uma tag",
            permission = Permission.NONE,
            usage = "/hg tag [tag]",
            min = 0,
            max = 1,
            console = false
    )
    fun tagCommand(sender: CommandSender, strings: Array<String>) {
        if (strings.isEmpty()) {
            val tags: MutableList<TagCommon> = ArrayList()

            TagCommon.values().forEach { tagCommon ->
                if (sender.hasPermission("${Permission.TAG.node}${tagCommon.name.toLowerCase()}")) {
                    tags.add(tagCommon)
                }
            }

            if (tags.isEmpty()) {
                sender.sendMessage("§cVocê não possui nenhuma tag!")
                return
            }

            sender.sendMessage(" ")

            tags.forEach { tagCommon ->
                val component = TextComponent(
                        tagCommon.nameColor
                                + tagCommon.name.replace("_", "").toUpperCase()
                )

                component.clickEvent = ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        "/hg tag " + tagCommon.name.replace("_", "").toLowerCase()
                )
                component.hoverEvent = HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        TextComponent.fromLegacyText("§eClique para selecionar esta tag!")
                )

                (sender as Player).spigot().sendMessage(component)
            }

            sender.sendMessage(" ")
            sender.sendMessage("§fUse §7/§ftag §a<§eNome da Tag§e> §fOu clique na §eTag §fDesejada")
        } else if (strings.size == 1) {
            val player = sender as Player

            val using = TagCommon.getTag(player)
            var toUse = TagCommon.PLAYER
            val str = strings[0]

            TagCommon.values().forEach { tagCommon ->
                if (tagCommon.name.replace("_".toRegex(), "").toLowerCase().startsWith(str)) {
                    toUse = tagCommon
                    return@forEach
                }
            }

            if (using.formattedNametag == toUse.formattedNametag) {
                player.sendMessage("§cVocê já está usando esta tag!")
                return
            }

            if (player.hasPermission("${Permission.TAG.node}${toUse.name.toLowerCase()}")) {
                player.sendMessage(" ")
                player.sendMessage(
                        "Agora você está usando a tag: §7'" + toUse.nameColor + toUse.name.toLowerCase()
                                .replace("_".toRegex(), "").toUpperCase() + "§7'"
                )

                TagCommon.setTag(sender, toUse)
            } else {
                if (toUse === TagCommon.PLAYER) {
                    player.sendMessage(" ")
                    player.sendMessage(
                            "Agora você está usando a tag: §7'" + toUse.nameColor + toUse.name.toLowerCase()
                                    .replace("_".toRegex(), "").toUpperCase() + "§7'"
                    )
                    TagCommon.setTag(player, toUse)
                } else player.sendMessage("§cDesculpe, mas você não possui permissão para usar esta tag.")
            }
        }
    }

    @Command(
            aliases = ["start", "iniciar", "st"],
            description = "Inicia a partida",
            permission = Permission.START,
            gameStage = [GameStage.PREGAME],
            usage = "/start"
    )
    fun onStartCommand(sender: CommandSender, strings: Array<String>) {
        if (Values.EVENT_MODE) {
            Timer.start(GameStage.INVINCIBLE, 600)
        } else {
            Timer.start(GameStage.INVINCIBLE, 180)
        }
    }

    @Command(
            aliases = ["feast"],
            description = "Aponta para o feast",
            permission = Permission.NONE,
            gameStage = [GameStage.GAME],
            usage = "/feast"
    )
    fun onFeastCommand(sender: CommandSender, strings: Array<String>) {
        if (Timer.gameTime >= Values.START_FEAST) {
            (sender as Player).compassTarget = FeastManager.location
            sender.sendMessage("${Strings.PREFIX} §fBússola apontando para o §aFeast §f!")
        }
    }

    @Command(
            aliases = ["minifeast"],
            description = "Aponta para o último mini-feast",
            permission = Permission.NONE,
            gameStage = [GameStage.GAME],
            usage = "/minifeast"
    )
    fun onMiniFeastCommand(sender: CommandSender, strings: Array<String>) {
        if (Timer.gameTime >= Values.MINI_FEAST_INTERVAL) {
            (sender as Player).compassTarget = MiniFeastManager.location
            sender.sendMessage("${Strings.PREFIX} §fBússola apontando para o último §aMiniFeast §f!")
        }
    }

    @Command(
            aliases = ["topkill", "tk"],
            description = "Mostra o top kill",
            permission = Permission.NONE,
            gameStage = [GameStage.GAME, GameStage.WIN],
            usage = "/topkill"
    )
    fun onTopKillCommand(sender: CommandSender, strings: Array<String>) {
        val playing = RobotoPlayerManager.getPlaying()

        val topKillList =
                playing.parallelStream().sorted(compareBy { t -> PlayerData.getData(t.player.uniqueId)?.kills })
                        .filter { t -> PlayerData.getData(t.player.uniqueId)!!.kills > 0 }
                        .map { t -> t.player.name }.limit(10).collect(Collectors.toList())

        val nameList: MutableList<String> = ArrayList()

        nameList.add("§a§k::§r §fTOP §eKILL §a§k::")

        topKillList.forEach { t ->
            nameList.add("§f$t §e${PlayerData.getData(Bukkit.getPlayer(t).uniqueId)?.kills}")
        }

        val topKillHologram = HologramManager.createHologram(
                (sender as Player).location.add(sender.location.direction.multiply(1.5)).add(.0, 3.0, .0),
                nameList.toTypedArray(),
                sender
        )

        object : BukkitRunnable() {
            override fun run() {
                topKillHologram.destroy()
            }
        }.runTaskLaterAsynchronously(RobotoHG.INSTANCE, 60L)
    }

    @Command(
            aliases = ["scoreboard", "sb"],
            description = "Ativa ou desativa a scoreboard",
            permission = Permission.NONE,
            usage = "/scoreboard"
    )
    fun onScoreboardCommand(sender: CommandSender, strings: Array<String>) {
        if (sender is Player) {
            val robotoPlayer = RobotoPlayerManager.getrobotoPlayer(sender)

            robotoPlayer.scoreboard()
        }
    }

    @Command(
            aliases = ["status"],
            description = "Abre o menu de status",
            permission = Permission.NONE,
            gameStage = [GameStage.PREGAME],
            usage = "/status"
    )
    fun onStatusCommand(sender: CommandSender, strings: Array<String>) {
        if (sender is Player) {
            InventoryManager.createInventory(sender, "status")
        }
    }

    @Command(
            aliases = ["skin", "sk"],
            description = "Troca a skin",
            permission = Permission.SKIN,
            usage = "/skin [nick",
            min = 0,
            max = 1
    )
    fun onSkinCommand(sender: CommandSender, strings: Array<String>) {
        if (sender is Player) {
            if (strings.isEmpty()) {
                if (SkinManager.using(sender)) {
                    SkinManager.restore(sender)

                    sender.sendMessage("${Strings.PREFIX} §fVocê voltou a sua skin original")
                } else {
                    sender.sendMessage("${Strings.PREFIX} §fVocê não trocou sua skin!")
                }
            } else {
                if (strings.size == 1) {
                    val skin = strings[0]

                    if (skin.isNotEmpty()) {
                        if (SkinManager.change(sender, skin, false)) {
                            sender.sendMessage("${Strings.PREFIX} §fSkin alterada com sucesso")
                        } else {
                            sender.sendMessage("§cVocê já está utilizando essa skin!")
                        }
                    } else {
                        sender.sendMessage("${Strings.PREFIX} §fSkin inválida!")
                    }
                } else {
                    sender.sendMessage("§c/hg skin [name]")
                }
            }
        }
    }

    @Command(
            aliases = ["fake", "fk"],
            description = "Troca o nick",
            permission = Permission.FAKE,
            usage = "/fake [nick, #]",
            min = 0,
            max = 1
    )
    fun onFakeCommand(sender: CommandSender, strings: Array<String>) {
        if (sender is Player) {
            if (strings.isEmpty()) {
                if (FakeManager.inFake(sender.name)) {
                    FakeManager.setFake(
                            sender,
                            FakeManager.getOriginalName(sender.name),
                            FakeManager.getOriginalName(sender.name)
                    )

                    sender.sendMessage("${Strings.PREFIX} §fVocê voltou ao seu nick original")
                } else {
                    sender.sendMessage("${Strings.PREFIX} §fVocê não está usando fake §e!")
                }
            } else {
                if (strings.size == 1) {
                    val nick = strings[0]

                    if (nick == "#") {
                        val randomNick = FakeManager.getRandomNick()

                        FakeManager.setFake(sender, randomNick, randomNick)

                        sender.sendMessage("${Strings.PREFIX} §fVocê passou a ser: §a${sender.name}")
                    } else if (nick == "list" && sender.isOp) {
                        val fakes = FakeManager.getFakes()

                        if (fakes.isNotEmpty()) {
                            fakes.forEach { t -> sender.sendMessage(t) }
                        } else {
                            sender.sendMessage("${Strings.PREFIX} §fNinguém utilizando fake §e!")
                        }

                    } else {
                        if (nick != sender.name) {
                            if (Bukkit.getPlayer(nick) == null) {
                                FakeManager.setFake(sender, nick, nick)
                            } else {
                                sender.sendMessage("§cErro§e! §fEste jogador está online§e!")
                            }
                        } else {
                            sender.sendMessage("§cErro§e! §fVocê já está usando este nick§e!")
                        }
                    }
                }
            }
        }
    }

    @Command(
            aliases = ["point", "pt"],
            description = "Aponta a bússola para uma determinada localização",
            permission = Permission.NONE,
            min = 1,
            max = 2,
            gameStage = [GameStage.GAME, GameStage.INVINCIBLE],
            player = true,
            usage = "/point [x, z] | [player]"
    )
    fun onPointCommand(sender: CommandSender, strings: Array<String>) {
        val player = sender as Player

        if (strings.isEmpty()) {
            player.sendMessage("§c/point [x, z] | [player]")
        } else {
            when (strings.size) {
                1 -> {
                    val target = strings[0]

                    if (Bukkit.getPlayer(target) == null) {
                        player.sendMessage("§cNão foi possível encontrar esste jogador §e!")
                    } else {
                        val targetPlayer = Bukkit.getPlayer(target)

                        player.compassTarget = targetPlayer.location

                        player.sendMessage("${Strings.PREFIX} §fBússola apontando para §a${targetPlayer.name}")
                    }
                }

                2 -> {
                    val x: Double
                    val z: Double

                    try {
                        x = strings[0].toDouble()
                        z = strings[1].toDouble()
                    } catch (e: NumberFormatException) {
                        player.sendMessage("§cUse apenas números!")
                        return
                    }

                    val locationRange: Range<Int> = Range.between(-500, 500)

                    if (locationRange.contains(x.toInt()) && locationRange.contains(z.toInt())) {
                        val targetLocation = Location(
                                player.world,
                                x,
                                player.world.getHighestBlockYAt(x.toInt(), z.toInt()).toDouble(),
                                z
                        )

                        player.compassTarget = targetLocation

                        player.sendMessage("${Strings.PREFIX} §fBússola apontando para §a$x §f| §a$z")
                    } else {
                        player.sendMessage("§cUse valores de §f-500 §ca §f500")
                    }
                }
            }
        }
    }
}
