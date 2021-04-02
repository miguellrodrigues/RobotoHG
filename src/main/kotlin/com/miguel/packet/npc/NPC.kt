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

package com.miguel.packet.npc

import com.miguel.RobotoHG
import com.miguel.game.util.GameProfileBuilder
import com.miguel.game.util.Skin
import com.miguel.packet.PacketUtil
import com.miguel.reflection.NMSUtil
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import net.minecraft.server.v1_8_R3.*
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class NPC(private var tablistName: String, var displayName: String, var location: Location, var player: Player?) {

    var id = 0

    private var profile = getProfile()!!

    private var inHand = Material.AIR

    private var helment = Material.AIR
    private var chestPlate = Material.AIR
    private var leggings = Material.AIR
    private var boots = Material.AIR

    private lateinit var entityPlayer: EntityPlayer

    private lateinit var dataWatcher: DataWatcher

    private fun getFixRotation(yawPitch: Float): Byte {
        return (yawPitch * 256.0f / 360.0f).toInt().toByte()
    }

    private var destroyed = false

    lateinit var action: Action

    fun spawn(tabList: Boolean) {
        updateProfile()

        entityPlayer = EntityPlayer(
                MinecraftServer.getServer(),
                MinecraftServer.getServer().getWorldServer(0),
                profile,
                PlayerInteractManager(MinecraftServer.getServer().getWorldServer(0))
        )

        entityPlayer.setLocation(
                location.x,
                location.y,
                location.z,
                location.pitch,
                location.yaw
        )

        id = entityPlayer.id

        addToTablist()

        try {
            val packet = PacketPlayOutNamedEntitySpawn(entityPlayer)

            sendPacket(packet)

            this.destroyed = false
        } catch (e: Exception) {
            throw Error(e.message)
        }

        this.dataWatcher = entityPlayer.dataWatcher

        action = Action(this)

        if (!tabList) {
            object : BukkitRunnable() {
                override fun run() {
                    removeFromTablist()
                }
            }.runTaskLater(RobotoHG.INSTANCE, 5L)
        }
    }

    fun destroy() {
        val packet = PacketPlayOutEntityDestroy(entityPlayer.id)

        removeFromTablist()

        sendPacket(packet)

        this.destroyed = true
    }

    private fun addToTablist() {
        try {
            val packet = PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer)

            sendPacket(packet)
        } catch (e: Exception) {
            throw Error(e.message)
        }
    }

    fun removeFromTablist() {
        try {
            val packet =
                    PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer)

            sendPacket(packet)
        } catch (e: Exception) {
            throw Error(e.message)
        }
    }

    fun teleport(location: Location) {
        entityPlayer.setLocation(
                location.x,
                location.y,
                location.z,
                location.yaw,
                location.pitch
        )

        try {
            val packet = PacketPlayOutEntityTeleport(entityPlayer)

            this.location = location

            val packetEntityLook = PacketPlayOutEntity.PacketPlayOutEntityLook(
                    entityPlayer.id,
                    getFixRotation(location.yaw),
                    getFixRotation(location.pitch),
                    true
            )

            val packetHead =
                    PacketPlayOutEntityHeadRotation(entityPlayer, getFixRotation(location.yaw))

            sendPacket(packet)
            sendPacket(packetEntityLook)
            sendPacket(packetHead)
        } catch (e: Exception) {
            throw Error(e.message)
        }
    }

    fun setItem(slot: Int, type: Material?) {
        try {
            val packet = PacketPlayOutEntityEquipment(
                    entityPlayer.id,
                    slot,
                    if (type == Material.AIR || type == null) CraftItemStack.asNMSCopy(
                            ItemStack(
                                    Material.AIR
                            )
                    ) else CraftItemStack.asNMSCopy(ItemStack(type))
            )

            when (slot) {
                1 -> {
                    helment = type!!
                }

                2 -> {
                    chestPlate = type!!
                }


                3 -> {
                    leggings = type!!
                }

                4 -> {
                    boots = type!!
                }

                5 -> {
                    inHand = type!!
                }
            }

            sendPacket(packet)
        } catch (e: Exception) {
            throw Error(e.message)
        }
    }

    private fun getSkin(): Property? {
        if (this.profile.properties.isEmpty) {
            return null
        }

        return this.profile.properties.get("textures").toList()[0]
    }

    fun setDisplayNameAboveHead(name: String) {
        if (name.length > 16)
            throw Exception("Nome maior que 16 caracteres")

        this.displayName = name
        reloadNpc()
    }

    fun setTabName(name: String) {
        this.tablistName = name
        updateToTablist()
    }

    private fun reloadNpc() {
        this.updateProfile()

        if (!this.destroyed) {
            val destroy = PacketPlayOutEntityDestroy(entityPlayer.id)

            sendPacket(destroy)

            removeFromTablist()

            this.spawn(false)
        }
    }

    private fun updateProfile() {
        if (player != null) {
            val craftPlayer = player as CraftPlayer

            if (craftPlayer.profile.name == tablistName) {
                this.profile = GameProfile(UUID.randomUUID(), displayName)
                NMSUtil.setValue(this.profile, "properties", craftPlayer.profile.properties)
            }
        } else {
            this.profile = getProfile()!!
        }
    }

    private fun updateToTablist() {
        val packet =
                PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, entityPlayer)

        sendPacket(packet)
    }

    fun setSleep(state: Boolean) {
        if (state) {
            val bed = Location(this.location.world, 0.0, 0.0, 0.0)

            val packet = PacketPlayOutBed(entityPlayer, BlockPosition(bed.x, bed.y, bed.z))

            sendPacket(packet)
            this.teleport(location.clone().add(0.0, 0.3, 0.0))
        } else {
            this.setAnimation(NPCAnimation.LEAVE_BED)
            this.teleport(location.clone().add(0.0, 0.3, 0.0))
        }
    }

    private fun setStatus(status: Byte) {
        val packet = PacketPlayOutEntityStatus(entityPlayer, status)
        sendPacket(packet)
    }

    fun setStatus(status: NPCStatus) {
        this.setStatus(status.id.toByte())
    }

    fun setEffect(effet: MobEffect) {
        sendPacket(PacketPlayOutEntityEffect(entityPlayer.id, effet))
    }

    private fun setAnimation(animation: Int) {
        /*ID	Animation
        0	Swing main arm
        1	Take damage
        2	Leave bed
        3	Swing offhand
        4	Critical effect
        5	Magic critical effect*/

        val animation = PacketPlayOutAnimation(entityPlayer, animation)

        sendPacket(animation)
    }

    fun setAnimation(animation: NPCAnimation) {
        this.setAnimation(animation.id)
    }

    fun update() {
        sendPacket(PacketPlayOutEntityMetadata(entityPlayer.id, entityPlayer.dataWatcher, true))
    }

    enum class NPCAnimation(val id: Int) {
        SWING_MAIN_HAND(0), TAKE_DAMAGE(1), LEAVE_BED(2), SWING_OFFHAND(3), CRITICAL_EFFECT(4), MAGIC_CRITICAL_EFFECT(5);
    }

    enum class NPCStatus(val id: Int) {
        HURT(2), DIE(3);
    }

    class Action(private val npc: NPC) {

        private var onFire = false

        private var isCrouched = false

        private var isSprinting = false

        private var isInvisible = false

        fun onFire(): Action {
            onFire = if (onFire) {
                npc.entityPlayer.setOnFire(0)
                false
            } else {
                npc.entityPlayer.setOnFire(1)
                true
            }
            return this
        }

        fun setCrouched(): Action {
            if (isCrouched) {
                npc.entityPlayer.isSneaking = false
                isCrouched = false
            } else {
                npc.entityPlayer.isSneaking = true
                isCrouched = true
            }
            return this
        }

        fun setSprinting(): Action {
            if (isSprinting) {
                npc.entityPlayer.isSprinting = false
                isSprinting = false
            } else {
                npc.entityPlayer.isSprinting = true
                isSprinting = true
            }
            return this
        }

        fun setInvisible(): Action {
            if (isInvisible) {
                npc.entityPlayer.isInvisible = false
                isInvisible = false
            } else {
                npc.entityPlayer.isInvisible = true
                isInvisible = true
            }
            return this
        }

        fun build() {
            npc.update()
        }
    }

    private fun sendPacket(packet: Packet<*>) {
        if (player == null) {
            PacketUtil.sendPacket(packet)
        } else {
            PacketUtil.sendPacket(player!!, packet)
        }
    }

    private fun getProfile(): GameProfile? {
        val profile = GameProfile(UUID.randomUUID(), displayName)

        try {
            val skin = Skin(
                    GameProfileBuilder.UUIDFetcher.getUUID(ChatColor.stripColor(tablistName))
            )

            val propertyMap = PropertyMap()
            propertyMap.put(skin.name, Property("textures", skin.value, skin.signature))

            val properties = profile.javaClass.getDeclaredField("properties")
            properties.isAccessible = true
            properties[profile] = propertyMap
        } catch (e: Exception) {
            return profile
        }

        return profile
    }
}