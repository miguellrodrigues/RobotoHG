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

package com.miguel.listener.habilities

import com.miguel.game.GameStage
import com.miguel.game.Timer
import com.miguel.game.kit.Kit
import com.miguel.game.manager.GameManager
import net.minecraft.server.v1_8_R3.*
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

class Specialist : Kit() {

    override fun getItems(): MutableList<ItemStack> {
        return arrayListOf(
                GameManager.createItem("§f§lKit §eSpecialist", emptyArray(), Material.BOOK)
        )
    }

    override fun getIcon(): ItemStack {
        return GameManager.createItem(
                "§f§lKit §eSpecialist",
                arrayOf(" ", "§fTenha uma mesa de encantamentos portátil!", " ", "§eClique para selecionar"),
                Material.BOOK
        )
    }

    override fun getPrice(): Int {
        return 0
    }

    override val cooldownMap: HashMap<UUID, Long> = HashMap()

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        if (using(player)) {
            if (player.itemInHand.type == Material.BOOK && Timer.gameStage != GameStage.PREGAME) {
                Enchanter(player).Roboto()
            }
        }
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity

        val killer = player.killer

        if (killer is Player) {
            if (using(killer)) {
                killer.level += 1
            }
        }
    }

    @EventHandler
    fun onPreItemEnchantEvent(event: PrepareItemEnchantEvent) {
        val enchanter = event.enchanter

        if (using(enchanter)) {
            event.expLevelCostsOffered[0] = 1
            event.expLevelCostsOffered[1] = (1..5).random()
            event.expLevelCostsOffered[2] = (1..10).random()
        }
    }

    private class Enchanter(player: Player) {

        private val entityPlayer: EntityPlayer = (player as CraftPlayer).handle
        private val container = EnchantmentContainer(entityPlayer)

        fun Roboto() {
            val c = entityPlayer.nextContainerCounter()

            entityPlayer.playerConnection.sendPacket(
                    PacketPlayOutOpenWindow(
                            c,
                            "minecraft:enchanting_table",
                            ChatComponentText("§eEncantar")
                    )
            )

            container.setItem(1, CraftItemStack.asNMSCopy(ItemStack(Material.INK_SACK, 3, 4)))

            entityPlayer.activeContainer = container
            entityPlayer.activeContainer.windowId = c
            entityPlayer.activeContainer.addSlotListener(entityPlayer)
            entityPlayer.activeContainer.checkReachable = false
        }

        private class EnchantmentContainer(entity: EntityHuman) :
                ContainerEnchantTable(entity.inventory, entity.world, BlockPosition(0, 0, 0)) {

            override fun c(human: EntityHuman): Boolean {
                return true
            }
        }
    }
}