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

package com.miguel.game.manager

import com.miguel.game.util.GameProfileBuilder
import com.miguel.game.util.Skin
import com.miguel.packet.PacketUtil
import com.miguel.reflection.NMSUtil
import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import net.minecraft.server.v1_8_R3.*
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

object SkinManager {

    private val skins = HashMap<UUID, String>()

    private const val value =
            "eyJ0aW1lc3RhbXAiOjE0NjQyOTYyOTU0MzksInByb2ZpbGVJZCI6Ijg2NjdiYTcxYjg1YTQwMDRhZjU0NDU3YTk3MzRlZWQ3IiwicHJvZmlsZU5hbWUiOiJTdGV2ZSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDU2ZWVjMWMyMTY5YzhjNjBhN2FlNDM2YWJjZDJkYzU0MTdkNTZmOGFkZWY4NGYxMTM0M2RjMTE4OGZlMTM4In0sIkNBUEUiOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iNzY3ZDQ4MzI1ZWE1MzI0NTYxNDA2YjhjODJhYmJkNGUyNzU1ZjExMTUzY2Q4NWFiMDU0NWNjMiJ9fX0="
    private const val signature =
            "TOyYc+LBQ5wyGVqXaDvTFejzVOC+ZnsqnXSi9PP4MmCSeU7h0DG6ZwrQbJD3S76wfd+hdIOJurhXW4d/vrDbP4AMUaZzpPRupEZicFxFAl1ZtdtFwzeLYX7COYSLF5nrUy1MSwAN40TnxaEQroLsYFjH0jsqtYLxP1s0WiyNjrEjJ9gWwEPY0fdlNNKjCECYg8vqMafnwsegVifUN8mPJWHykgfGf0sa80nKVTEaNApbbTEHM+EGoU3MDkce65O7tKtSTy979zoKXK+XaJtCQvK5C3s1K49jnxJRHfXcQDW7t6K0VKAoTa5sw/JK4+WmPRNv5eRwOJmGhEcAs1+PN25JB5n+4X/kK2P2eyesIc4DhCUrle+sMifFtaxV6QA15z622wR2XzkUrfiyQyG1b4IuZjuEQcMO/u+rT0PT/Mn5PnofUDagSt/zni+lDT/c8ItXCp1h3oAcMmZ0l4rArIXTeeu6RgRepdrOvKJNr7LjdoHJR9iVCL42GAuEUnwujySKkGP7WfyES9+au7ujPBQhMauMiLFJwoN5RQ9yhv4n2TGwFQ2YArhD4eihDcZ5r/UbpkP9eOS3+C8XZNAK7emrhzob4zFfFTjUBAHxZ92ku9o7Y+PEQN+xItUu70A2aUHJGnE+DEWpaUN7MJmarVhbMZUxuAXjEZiaOjs45z0="

    fun using(player: Player): Boolean {
        return player.uniqueId in skins
    }

    private fun getSkinName(player: Player): String {
        if (using(player)) {
            return skins[player.uniqueId]!!
        }

        return ""
    }

    fun restore(player: Player) {
        change(player, (player as CraftPlayer).profile.name, fake = false)

        skins.remove(player.uniqueId)
    }

    fun change(player: Player, skin: String, fake: Boolean): Boolean {
        if (getSkinName(player) == skin && !fake)
            return false

        val entityPlayer = (player as CraftPlayer).handle

        val propertyMap = PropertyMap()

        val contents = player.inventory.contents
        val armorContents = player.inventory.armorContents

        val gameMode = entityPlayer.bukkitEntity.gameMode

        val remove = PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer
        )

        PacketUtil.sendPacket(remove)

        try {
            val sk = Skin(
                    GameProfileBuilder.UUIDFetcher.getUUID(skin)
            )

            propertyMap.put("textures", Property("textures", sk.value, sk.signature))
        } catch (e: Exception) {
            propertyMap.put("textures", Property("textures", value, signature))
        }

        NMSUtil.setValue(entityPlayer.profile, "properties", propertyMap)

        val respawn = PacketPlayOutRespawn(
                0,
                EnumDifficulty.NORMAL,
                WorldType.NORMAL,
                WorldSettings.EnumGamemode.valueOf(gameMode.name)
        )

        entityPlayer.playerConnection.sendPacket(respawn)

        player.teleport(player.location.add(.0, .1, .0))

        player.inventory.contents = contents
        player.inventory.armorContents = armorContents

        player.updateInventory()

        val add = PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer)
        PacketUtil.sendPacket(add)

        GameManager.players.forEach { t ->
            t.hidePlayer(player)
            t.showPlayer(player)
        }

        if (!fake)
            skins[player.uniqueId] = skin

        return true
    }
}