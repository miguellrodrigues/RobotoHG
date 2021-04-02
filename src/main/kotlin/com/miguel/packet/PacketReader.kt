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

import com.miguel.packet.npc.NPC
import com.miguel.packet.npc.NpcManager
import com.miguel.reflection.NMSUtil
import io.netty.channel.Channel
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import net.minecraft.server.v1_8_R3.Packet
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player

class PacketReader(private val player: Player) {

    private val npc = NpcManager.getNpc(player)

    private lateinit var channel: Channel

    fun inject() {
        val cPlayer = player as CraftPlayer

        channel = cPlayer.handle.playerConnection.networkManager.channel

        uninject()

        val duplexHandler: ChannelDuplexHandler = object : ChannelDuplexHandler() {
            override fun channelRead(context: ChannelHandlerContext, `object`: Any) {
                readPacket(`object` as Packet<*>)
                super.channelRead(context, `object`)
            }

            override fun write(context: ChannelHandlerContext, `object`: Any, promise: ChannelPromise) {
                super.write(context, `object`, promise)
            }
        }

        channel.pipeline().addBefore("packet_handler", player.name, duplexHandler)
    }

    private fun uninject() {
        if (channel.pipeline()[player.name] != null) {
            channel.pipeline().remove(player.name)
        }
    }

    fun readPacket(packet: Packet<*>) {
        if (packet is PacketPlayInUseEntity) {
            val id = NMSUtil.getValue(packet, "a") as Int

            if (npc?.id == id) {
                val action = NMSUtil.getValue(packet, "action") as PacketPlayInUseEntity.EnumEntityUseAction

                if (action.name.contains("INTERACT")) {
                    npc.action.setCrouched().build()
                } else {
                    npc.setAnimation(NPC.NPCAnimation.SWING_MAIN_HAND)
                }
            }
        }
    }
}