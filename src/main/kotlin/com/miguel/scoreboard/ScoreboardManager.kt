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

package com.miguel.scoreboard

import com.miguel.RobotoHG
import com.miguel.scoreboard.lib.type.Entry
import com.miguel.scoreboard.lib.type.ScoreboardHandler
import net.minecraft.server.v1_8_R3.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class ScoreboardManager(private val player: Player?) {

    private lateinit var score: ScoreboardHandler
    private var delay: Long = 0
    private lateinit var task: BukkitRunnable
    private var activated = false

    fun setHandler(score: ScoreboardHandler): ScoreboardManager {
        this.score = score
        return this
    }

    fun setDelay(delay: Long): ScoreboardManager {
        this.delay = delay
        return this
    }

    fun build() {
        activated = true

        player?.scoreboard = Bukkit.getScoreboardManager().newScoreboard

        val sb = Scoreboard()
        val obj = sb.registerObjective("Robotohg", IScoreboardCriteria.b)

        obj.displayName = ChatColor.translateAlternateColorCodes(
                '&',
                score.getTitle(player).replace("&", "§")
        )

        val removePacket = PacketPlayOutScoreboardObjective(obj, 1)
        val createPacket = PacketPlayOutScoreboardObjective(obj, 0)

        val display = PacketPlayOutScoreboardDisplayObjective(1, obj)

        val craftPlayer = player as CraftPlayer

        craftPlayer.handle.playerConnection.sendPacket(removePacket)
        craftPlayer.handle.playerConnection.sendPacket(createPacket)
        craftPlayer.handle.playerConnection.sendPacket(display)

        task = object : BukkitRunnable() {
            var cache: List<Entry> =
                    ArrayList()

            override fun run() {
                if (!player.isOnline()) {
                    cancel()
                    return
                }

                val entries = score.getEntries(player)

                if (cache.isNotEmpty()) {
                    var refreshed = false
                    if (entries.size < cache.size) {
                        player.handle.playerConnection.sendPacket(removePacket)
                        player.handle.playerConnection.sendPacket(createPacket)
                        player.handle.playerConnection.sendPacket(display)
                        refreshed = true
                    }
                    if (!refreshed) {
                        entries.forEach { entry ->
                            cache.forEach { cached ->
                                if (entry.position == cached.position) {
                                    if (entry.name != cached.name) {
                                        val pa = PacketPlayOutScoreboardScore(
                                                cached.name
                                        )
                                        player.handle.playerConnection.sendPacket(pa)
                                    }
                                }
                            }
                        }
                    }
                }

                entries.forEach { entry ->
                    val a = ScoreboardScore(
                            sb, obj,
                            ChatColor.translateAlternateColorCodes('&', entry.name)
                    )
                    a.score = entry.position
                    val pa = PacketPlayOutScoreboardScore(a)
                    player.handle.playerConnection.sendPacket(pa)
                }

                cache = ArrayList(entries)
            }
        }

        task.runTaskTimerAsynchronously(RobotoHG.INSTANCE, 0L, delay)
    }

    fun destroy() {
        task.cancel()
        player?.scoreboard = Bukkit.getScoreboardManager().newScoreboard

        val sb = Scoreboard()
        val obj = sb.registerObjective("Robotohg", IScoreboardCriteria.b)

        obj.displayName = ChatColor.translateAlternateColorCodes(
                '&',
                score.getTitle(player).replace("&", "§")
        )

        val removePacket = PacketPlayOutScoreboardObjective(obj, 1)
        (player as CraftPlayer?)!!.handle.playerConnection.sendPacket(removePacket)

        activated = false
    }

    fun isActivated(): Boolean {
        return activated
    }
}