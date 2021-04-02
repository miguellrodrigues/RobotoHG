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

package com.miguel.packet

import com.miguel.reflection.NMSUtil
import com.miguel.reflection.TinyProtocol
import com.mojang.authlib.GameProfile
import io.netty.channel.Channel
import net.minecraft.server.v1_8_R3.PacketStatusOutServerInfo
import net.minecraft.server.v1_8_R3.ServerPing
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.ArrayList

class CustomPing(plugin: JavaPlugin, lines: Array<String>) {

    private val sample = ServerPing.ServerPingPlayerSample(0, 0)

    private val profiles: MutableList<GameProfile> = ArrayList()

    init {
        lines.forEach { line ->
            profiles.add(
                    GameProfile(UUID.randomUUID(), line)
            )
        }

        sample.a(profiles.toTypedArray())

        object : TinyProtocol(plugin) {
            override fun onPacketOutAsync(receiver: Player?, channel: Channel?, packet: Any?): Any {
                if (packet is PacketStatusOutServerInfo) {
                    //val b = packet.javaClass.getDeclaredField("b")
                    //b.isAccessible = true

                    //val serverPing = b.get(packet) as ServerPing

                    val serverPing = NMSUtil.getValue(packet, "b") as ServerPing

                    /*val d = serverPing.javaClass.getDeclaredField("c")
                    d.isAccessible = true*/

                    NMSUtil.setValue(serverPing, "c", ServerPing.ServerData("§f§lRoboto§e§lHG", 1))
                    //d.set(serverPing, ServerPing.ServerData("§f§lRoboto§e§lHG", 1))

                    serverPing.setPlayerSample(sample)

                    //b.set(packet, serverPing)
                    NMSUtil.setValue(packet, "b", serverPing)
                }

                return super.onPacketOutAsync(receiver, channel, packet)
            }
        }
    }
}