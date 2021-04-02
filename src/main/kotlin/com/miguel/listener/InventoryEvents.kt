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

package com.miguel.listener

import com.miguel.RobotoHG
import com.miguel.game.GameStage
import com.miguel.game.Timer
import com.miguel.game.kit.KitManager
import com.miguel.game.manager.FeastManager
import com.miguel.game.manager.GameManager
import com.miguel.game.manager.InventoryManager
import com.miguel.util.Strings
import com.miguel.util.Values
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

class InventoryEvents : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked

        if (player !is Player)
            return

        val inventory = event.inventory

        val currentItem = event.currentItem

        if (inventory.type == InventoryType.ENCHANTING) {
            if (currentItem == null || currentItem.type == Material.AIR)
                return

            if (currentItem.type == Material.INK_SACK && currentItem.data.data == 4.toByte())
                event.isCancelled = true
        }

        if (InventoryManager.has(inventory)) {
            if (currentItem == null || currentItem.type == Material.AIR)
                return

            event.isCancelled = true

            if (inventory.title.startsWith("§e§lKit §f§lSelector 1")) {
                if (currentItem.type == Material.CHEST) {
                    InventoryManager.createInventory(player, "secondary_kit_selector")
                } else if (currentItem.type == Material.INK_SACK) {
                    var page =
                            ChatColor.stripColor(inventory.title.replace("§e§lKit §f§lSelector 1     §fPágina §e", ""))
                                    .toInt()

                    if (currentItem.type == Material.CHEST) {
                        InventoryManager.kitSelector(player, 1, 1)
                    } else if (currentItem.type == Material.INK_SACK) {
                        when (currentItem.data.data) {
                            10.toByte() -> {
                                InventoryManager.kitSelector(player, 0, ++page)
                            }

                            12.toByte() -> {
                                InventoryManager.kitSelector(player, 0, --page)
                            }
                        }
                    }
                }

                if (currentItem.itemMeta.hasDisplayName()) {
                    val name = ChatColor.stripColor(currentItem.itemMeta.displayName)

                    if (name.toLowerCase().startsWith("kit")) {
                        val kit = KitManager.getByName(name.toLowerCase().replace("kit ", ""))!!

                        if (KitManager.getKits(player).contains(kit)) {
                            player.closeInventory()

                            player.sendMessage("${Strings.PREFIX} §fVocê já selecionou este kit §e!")

                            player.playSound(
                                    player.location,
                                    Sound.NOTE_BASS_GUITAR,
                                    1.0F,
                                    1.0F
                            )
                        } else {
                            KitManager.setKit(
                                    player,
                                    kit,
                                    0
                            )

                            player.closeInventory()

                            GameManager.sendTitle(
                                    player,
                                    "§eKit §f${name.replace("Kit ", "")}",
                                    PacketPlayOutTitle.EnumTitleAction.TITLE
                            )

                            GameManager.sendTitle(
                                    player,
                                    "§fSelecionado com sucesso!",
                                    PacketPlayOutTitle.EnumTitleAction.SUBTITLE
                            )

                            player.playSound(player.location, Sound.ORB_PICKUP, 1.0F, 1.0F)
                        }
                    }
                }
            } else if (inventory.title.startsWith("§e§lKit §f§lSelector 2")) {
                var page =
                        ChatColor.stripColor(inventory.title.replace("§e§lKit §f§lSelector 2     §fPágina §e", ""))
                                .toInt()

                if (currentItem.type == Material.CHEST) {
                    InventoryManager.kitSelector(player, 0, 1)
                } else if (currentItem.type == Material.INK_SACK) {
                    when (currentItem.data.data) {
                        10.toByte() -> {
                            InventoryManager.kitSelector(player, 1, ++page)
                        }

                        12.toByte() -> {
                            InventoryManager.kitSelector(player, 1, --page)
                        }
                    }
                }

                if (currentItem.itemMeta.hasDisplayName()) {
                    val name = ChatColor.stripColor(currentItem.itemMeta.displayName)

                    if (name.toLowerCase().startsWith("kit")) {
                        val kit = KitManager.getByName(name.toLowerCase().replace("kit ", ""))!!

                        if (KitManager.getKits(player).contains(kit)) {
                            player.closeInventory()

                            player.sendMessage("${Strings.PREFIX} §fVocê já selecionou este kit §e!")

                            player.playSound(
                                    player.location,
                                    Sound.NOTE_BASS_GUITAR,
                                    1.0F,
                                    1.0F
                            )

                        } else {
                            KitManager.setKit(
                                    player,
                                    KitManager.getByName(name.toLowerCase().replace("kit ", ""))!!,
                                    1
                            )

                            player.closeInventory()

                            GameManager.sendTitle(
                                    player,
                                    "§eKit §f${name.replace("Kit ", "")}",
                                    PacketPlayOutTitle.EnumTitleAction.TITLE
                            )
                            GameManager.sendTitle(
                                    player,
                                    "§fSelecionado com sucesso!",
                                    PacketPlayOutTitle.EnumTitleAction.SUBTITLE
                            )

                            player.playSound(player.location, Sound.ORB_PICKUP, 1.0F, 1.0F)
                        }
                    }
                }
            }

            when (inventory.title) {
                "§e§lBiomas" -> {
                    if (currentItem.itemMeta.hasDisplayName() && currentItem.itemMeta.displayName.startsWith("Bioma")) {
                        player.closeInventory()

                        val lore = currentItem.itemMeta.lore

                        val x = ChatColor.stripColor(lore[0]).replace("X ", "").toDouble()
                        val z = ChatColor.stripColor(lore[1]).replace("Z ", "").toDouble()

                        val targetLocation = Location(
                                player.world,
                                x,
                                player.world.getHighestBlockYAt(x.toInt(), z.toInt()).toDouble(),
                                z
                        )

                        player.compassTarget = targetLocation
                    }
                }

                "§f§lConfig" -> {
                    var close = false

                    when (event.slot) {
                        0 -> {
                            player.performCommand("hg dano")
                        }

                        4 -> {
                            player.performCommand("hg chat")
                        }

                        8 -> {
                            player.teleport(FeastManager.location.clone().add(0.0, 10.0, 0.0))
                        }

                        13 -> {
                            player.performCommand("hg event")
                        }

                        27 -> {
                            if (!Values.FEAST_SPAWN) {
                                player.performCommand("hg ffeast")
                            }
                        }

                        31 -> {
                            close = true
                        }

                        35 -> {
                            player.performCommand("hg fminifeast")
                        }
                    }

                    player.closeInventory()

                    if (!close)
                        InventoryManager.createInventory(player, "config")
                }
            }
        }
    }

    @EventHandler
    fun onInventoryRoboto(event: InventoryOpenEvent) {
        val inventory = event.inventory

        if (inventory.type == InventoryType.ENCHANTING) {
            inventory.setItem(
                    1, ItemStack(
                    Material.INK_SACK, 3, 4
            )
            )
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val inventory = event.inventory

        if (InventoryManager.has(inventory)) {
            if (inventory.title == "Kit diário") {
                object : BukkitRunnable() {
                    override fun run() {
                        event.player.openInventory(inventory)
                    }
                }.runTaskLaterAsynchronously(RobotoHG.INSTANCE, 1L)
            } else {
                InventoryManager.remove(inventory)
            }
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player

        val action = event.action

        if (Timer.gameStage == GameStage.PREGAME) {
            if (action.name.contains("RIGHT")) {
                when (player.itemInHand.type) {
                    Material.STORAGE_MINECART -> {
                        InventoryManager.createInventory(player, "primary_kit_selector")
                    }

                    Material.BOOK -> {
                        InventoryManager.createInventory(player, "biomes")
                    }

                    Material.ENDER_CHEST -> {
                        InventoryManager.createInventory(player, "daily_kit")
                    }

                    Material.SKULL_ITEM -> {
                        player.performCommand("hg status")

                        /*val npc = NpcManager.getNpc(player)

                        if (npc == null) {
                            val spawnNpc = NpcManager.spawnNpc(
                                player, (player as CraftPlayer).profile.name, "§e§lSTATUS", false, Location(
                                    player.world,
                                    0.5, 201.5,
                                    9.5
                                )
                            )

                            PacketReader(player).inject()

                            player.teleport(
                                player.location.setDirection(
                                    spawnNpc.location.clone().subtract(player.location).toVector()
                                )
                            )
                        } else {
                            player.teleport(
                                player.location.setDirection(
                                    npc.location.clone().subtract(player.location).toVector()
                                )
                            )
                        }*/
                    }

                    else -> {
                    }
                }
            }
        }
    }
}