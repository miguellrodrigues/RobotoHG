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

import com.miguel.common.TagCommon
import com.miguel.packet.PacketUtil
import com.miguel.reflection.NMSUtil
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player

object FakeManager {

    private const val value =
            "eyJ0aW1lc3RhbXAiOjE0NjQyOTYyOTU0MzksInByb2ZpbGVJZCI6Ijg2NjdiYTcxYjg1YTQwMDRhZjU0NDU3YTk3MzRlZWQ3IiwicHJvZmlsZU5hbWUiOiJTdGV2ZSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDU2ZWVjMWMyMTY5YzhjNjBhN2FlNDM2YWJjZDJkYzU0MTdkNTZmOGFkZWY4NGYxMTM0M2RjMTE4OGZlMTM4In0sIkNBUEUiOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iNzY3ZDQ4MzI1ZWE1MzI0NTYxNDA2YjhjODJhYmJkNGUyNzU1ZjExMTUzY2Q4NWFiMDU0NWNjMiJ9fX0="
    private const val signature =
            "TOyYc+LBQ5wyGVqXaDvTFejzVOC+ZnsqnXSi9PP4MmCSeU7h0DG6ZwrQbJD3S76wfd+hdIOJurhXW4d/vrDbP4AMUaZzpPRupEZicFxFAl1ZtdtFwzeLYX7COYSLF5nrUy1MSwAN40TnxaEQroLsYFjH0jsqtYLxP1s0WiyNjrEjJ9gWwEPY0fdlNNKjCECYg8vqMafnwsegVifUN8mPJWHykgfGf0sa80nKVTEaNApbbTEHM+EGoU3MDkce65O7tKtSTy979zoKXK+XaJtCQvK5C3s1K49jnxJRHfXcQDW7t6K0VKAoTa5sw/JK4+WmPRNv5eRwOJmGhEcAs1+PN25JB5n+4X/kK2P2eyesIc4DhCUrle+sMifFtaxV6QA15z622wR2XzkUrfiyQyG1b4IuZjuEQcMO/u+rT0PT/Mn5PnofUDagSt/zni+lDT/c8ItXCp1h3oAcMmZ0l4rArIXTeeu6RgRepdrOvKJNr7LjdoHJR9iVCL42GAuEUnwujySKkGP7WfyES9+au7ujPBQhMauMiLFJwoN5RQ9yhv4n2TGwFQ2YArhD4eihDcZ5r/UbpkP9eOS3+C8XZNAK7emrhzob4zFfFTjUBAHxZ92ku9o7Y+PEQN+xItUu70A2aUHJGnE+DEWpaUN7MJmarVhbMZUxuAXjEZiaOjs45z0="

    private val fakes: HashMap<String, String> = HashMap()
    private val fakeList: MutableList<String> = ArrayList()

    fun inFake(name: String): Boolean {
        return fakes.containsKey(name) || fakeList.contains(name)
    }

    fun getOriginalName(name: String): String {
        if (inFake(name)) {
            return fakes[name]!!
        }

        return ""
    }

    fun getFakes(): MutableList<String> {
        val names: MutableList<String> = ArrayList()

        GameManager.players.forEach { player ->
            if (inFake(player.name)) {
                names.add("§fNick original: §a${getOriginalName(player.name)} §fNick fake: §e${player.name}")
            }
        }

        return names
    }

    private val names = listOf(
            "darkplugginer",
            "pele",
            "xuxa",
            "joao",
            "pedro",
            "maria",
            "Preens",
            "menstruatinq",
            "diepeep",
            "creeper",
            "naruto",
            "sasuke",
            "orochimaru",
            "saitama",
            "genos",
            "izayoi"
    )

    fun getRandomNick(): String {
        return names[(names.indices).random()]
    }

    fun setFake(player: Player, fake: String?, skin: String?) {
        val entityPlayer = (player as CraftPlayer).handle
        val oldName = player.name

        if (fakes.containsKey(oldName)) {
            val originalName = getOriginalName(player.name)

            if (originalName == fake!!) {
                fakeList.remove(originalName)
                fakes.remove(oldName)
            } else {
                fakes.remove(oldName)

                fakes[fake] = originalName
            }
        } else {
            fakes[fake!!] = oldName
            fakeList.add(oldName)
        }

        val remove = PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer
        )

        PacketUtil.sendPacket(remove)

        NMSUtil.setValue(entityPlayer.profile, "name", fake)

        player.setDisplayName(player.displayName.replace(player.name, fake))
        player.setPlayerListName(player.playerListName.replace(player.name, fake))

        SkinManager.change(player, fake, true)

        TagCommon.setTag(player, TagCommon.PLAYER)
    }
}