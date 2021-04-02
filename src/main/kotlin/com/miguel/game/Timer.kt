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

package com.miguel.game

import com.miguel.RobotoHG
import com.miguel.game.manager.FeastManager
import com.miguel.game.manager.GameManager
import com.miguel.game.manager.Manager
import com.miguel.game.manager.MiniFeastManager
import com.miguel.game.rplayer.RobotoPlayerManager
import com.miguel.util.Strings
import com.miguel.util.Values
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Sound
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

object Timer {

    var gameTime = 0

    lateinit var gameStage: GameStage

    private lateinit var task: BukkitTask

    init {
        start(GameStage.PREGAME, 120)
    }

    @Synchronized
    fun start(stage: GameStage, time: Int) {
        gameStage = stage
        gameTime = time

        when (stage) {
            GameStage.PREGAME -> {
                task = object : BukkitRunnable() {
                    override fun run() {
                        if (gameTime > 0)
                            gameTime--

                        if (gameTime % 30 == 0) {
                            if (gameTime == 0) {
                                if (GameManager.players.size < Values.MIN_PLAYERS) {
                                    GameManager.sendMessage("${Strings.PREFIX} §fJogadores insuficientes§e! §fReiniciando contagem")
                                    GameManager.playSound(Sound.NOTE_BASS)

                                    gameTime = 30
                                    return
                                }

                                if (Values.EVENT_MODE) {
                                    start(GameStage.INVINCIBLE, 600)
                                } else {
                                    start(GameStage.INVINCIBLE, 180)
                                }

                                cancel()
                            } else {
                                GameManager.sendMessage(
                                        "${Strings.PREFIX} §fPartida iniciando em §f${
                                            Manager.formatTime(
                                                    gameTime
                                            )
                                        }"
                                )
                                GameManager.playSound(Sound.CLICK)
                            }
                        }
                    }
                }.runTaskTimer(RobotoHG.INSTANCE, 0L, 20L)
            }

            GameStage.INVINCIBLE -> {
                task.cancel()

                GameManager.invencibility()

                task = object : BukkitRunnable() {
                    override fun run() {
                        if (gameTime > 0)
                            gameTime--

                        if (gameTime % 30 == 0) {
                            if (gameTime == 0) {
                                start(GameStage.GAME, 0)
                                cancel()
                            } else {
                                GameManager.sendMessage(
                                        "${Strings.PREFIX} §fA invencibilidade acaba em §f${
                                            Manager.formatTime(
                                                    gameTime
                                            )
                                        }"
                                )
                                GameManager.playSound(Sound.CLICK)
                            }
                        }
                    }
                }.runTaskTimer(RobotoHG.INSTANCE, 0L, 20L)
            }

            GameStage.GAME -> {
                task.cancel()

                GameManager.game()

                task = object : BukkitRunnable() {
                    override fun run() {
                        gameTime++

                        //GameManager.win()

                        if (gameTime % Values.MINI_FEAST_INTERVAL == 0) {
                            MiniFeastManager.launch()
                        }

                        when (gameTime) {
                            Values.START_FEAST -> {
                                FeastManager.launch()
                            }

                            else -> {
                            }
                        }
                    }
                }.runTaskTimer(RobotoHG.INSTANCE, 0L, 20L)
            }

            GameStage.WIN -> {
                task.cancel()

                val player = RobotoPlayerManager.getPlaying()[0].player

                GameManager.win(player)

                GameManager.firework(
                        player.location, Color.fromRGB(
                        (0..255).random(),
                        (0..255).random(),
                        (0..255).random()
                ), false
                )

                object : BukkitRunnable() {
                    var wt = 0

                    override fun run() {
                        wt++

                        if (wt % 3 == 0) {
                            GameManager.firework(
                                    player.location, Color.fromRGB(
                                    (0..255).random(),
                                    (0..255).random(),
                                    (0..255).random()
                            ), false
                            )
                        }

                        if (wt % 5 == 0) {
                            GameManager.sendMessage("${Strings.PREFIX} §fO jogador §a${player.name} §fVenceu a partida §a!")
                        }

                        if (wt == 20) {
                            Bukkit.shutdown()
                        }
                    }
                }.runTaskTimer(RobotoHG.INSTANCE, 0L, 20L)
            }

            else -> {

            }
        }
    }
}