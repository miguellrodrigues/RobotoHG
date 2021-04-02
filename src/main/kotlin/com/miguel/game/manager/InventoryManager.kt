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

import com.miguel.RobotoHG
import com.miguel.game.data.PlayerData
import com.miguel.game.kit.KitManager
import com.miguel.manager.PlayerManager
import com.miguel.util.Permissions
import com.miguel.util.Strings
import com.miguel.util.Values
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.scheduler.BukkitRunnable
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

object InventoryManager {


    private val inventories: MutableList<Inventory> = ArrayList()

    private var kitSelectorBaseInventory = Bukkit.createInventory(null, 54, "§e§lKit §f§lSelector 1")
    private var secondaryKitSelectorBaseInventory = Bukkit.createInventory(null, 54, "§e§lKit §f§lSelector 2")
    private val biomes = Bukkit.createInventory(null, 36, "§e§lBiomas")

    init {
        kitSelectorBaseInventory.setItem(0, GameManager.createItem("§fSem página anterior.", Material.INK_SACK, 8))
        kitSelectorBaseInventory.setItem(1, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 0))
        kitSelectorBaseInventory.setItem(2, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 0))
        kitSelectorBaseInventory.setItem(3, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 0))
        kitSelectorBaseInventory.setItem(4, GameManager.createItem("§eSeletor de kits 2.", Material.CHEST, 0))
        kitSelectorBaseInventory.setItem(5, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 0))
        kitSelectorBaseInventory.setItem(6, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 0))
        kitSelectorBaseInventory.setItem(7, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 0))
        kitSelectorBaseInventory.setItem(8, GameManager.createItem("§fSem próxima página.", Material.INK_SACK, 8))

        kitSelectorBaseInventory.setItem(9, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 4))
        kitSelectorBaseInventory.setItem(17, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 4))
        kitSelectorBaseInventory.setItem(18, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 4))
        kitSelectorBaseInventory.setItem(26, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 4))
        kitSelectorBaseInventory.setItem(27, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 4))
        kitSelectorBaseInventory.setItem(35, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 4))
        kitSelectorBaseInventory.setItem(36, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 4))
        kitSelectorBaseInventory.setItem(44, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 4))
        kitSelectorBaseInventory.setItem(45, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 4))
        kitSelectorBaseInventory.setItem(46, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 4))
        kitSelectorBaseInventory.setItem(47, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 4))
        kitSelectorBaseInventory.setItem(48, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 4))
        kitSelectorBaseInventory.setItem(49, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 0))

        kitSelectorBaseInventory.setItem(50, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 4))
        kitSelectorBaseInventory.setItem(51, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 4))
        kitSelectorBaseInventory.setItem(52, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 4))
        kitSelectorBaseInventory.setItem(53, GameManager.createItem("§e.", Material.STAINED_GLASS_PANE, 4))

        secondaryKitSelectorBaseInventory.contents = kitSelectorBaseInventory.contents
        secondaryKitSelectorBaseInventory.setItem(4, GameManager.createItem("§eSeletor de kits 1.", Material.CHEST, 0))

        val world = Bukkit.getWorld("world")

        val x1: Int
        val z1: Int

        val x2: Int
        val z2: Int

        if ((0..1).random() > 0.5) {
            x1 = 0
            x2 = 500

            z1 = 0
            z2 = 500
        } else {
            x1 = -500
            x2 = 0

            z1 = -500
            z2 = 0
        }

        for (x in x1..x2) {
            for (z in z1..z2) {
                if (x % 100 == 0 && z % 100 == 0) {
                    val location = Location(
                            world, x.toDouble(),
                            world.getHighestBlockYAt(x, z).toDouble(),
                            z.toDouble()
                    )

                    val biomeItem = GameManager.createItem(
                            "§fBioma de §e${Manager.getBiomeName(location.block)}",
                            arrayOf("§eX §f$x", "§eZ §f$z"),
                            Manager.getBiomeMaterial(location.block)
                    )

                    biomes.addItem(biomeItem)
                }
            }
        }

        addInventory(biomes)
    }

    private fun addInventory(inventory: Inventory) {
        if (!has(inventory)) {
            inventories.add(inventory)
        }
    }

    fun remove(inventory: Inventory) {
        inventories.remove(inventory)
    }

    fun has(inventory: Inventory): Boolean {
        return inventories.contains(inventory)
    }

    fun createInventory(player: Player, type: String) {
        when (type) {
            "primary_kit_selector" -> {
                kitSelector(player, 0, 1)
            }

            "secondary_kit_selector" -> {
                kitSelector(player, 1, 1)
            }

            "daily_kit" -> {
                val canDailyKit = GameManager.canDailyKit(player)

                if (canDailyKit == 1) {
                    val nextDay = Date.from(Instant.now().plus(1, ChronoUnit.DAYS))
                    val inv = Bukkit.createInventory(player, 27, "Kit diário")

                    addInventory(inv)

                    dailyKit(player, inv)

                    player.openInventory(inv)

                    PlayerData.getData(player.uniqueId)?.dailyKit = nextDay
                } else {
                    if (canDailyKit == 2) {
                        player.sendMessage("§cVocê já possui todos os kits")
                    } else {
                        val dif: Long = PlayerData.getData(player.uniqueId)!!.dailyKit.time - Date().time

                        val difSeconds = TimeUnit.MILLISECONDS.toSeconds(dif) % 60
                        val difMinutes = TimeUnit.MILLISECONDS.toMinutes(dif) % 60
                        val difHours = TimeUnit.MILLISECONDS.toHours(dif)

                        player.sendMessage("§cAguarde §7(${difHours}h§7) (§7${difMinutes}m) §7(${difSeconds}s§7) §fpara usar novamente")
                    }
                }
            }

            "biomes" -> {
                player.openInventory(biomes)
            }

            "config" -> {
                val config = Bukkit.createInventory(player, 36, "§f§lConfig")

                val location =
                        "§e(§f${FeastManager.location.blockX}§a, §f${FeastManager.location.blockY}§a, §f${FeastManager.location.blockZ}§e)"

                if (Values.GLOBAL_DAMAGE) {
                    config.setItem(
                            0,
                            GameManager.createItem("§aDano", emptyArray(), Material.DIAMOND_SWORD)
                    )
                } else {
                    config.setItem(
                            0,
                            GameManager.createItem("§cDano", emptyArray(), Material.GOLD_SWORD)
                    )
                }

                if (Values.GLOBAL_CHAT) {
                    config.setItem(
                            4,
                            GameManager.createItem("§aChat", emptyArray(), Material.RED_MUSHROOM)
                    )
                } else {
                    config.setItem(
                            4,
                            GameManager.createItem("§cChat", emptyArray(), Material.BROWN_MUSHROOM)
                    )
                }

                if (Values.OLD_FEAST) {
                    config.setItem(
                            8,
                            GameManager.createItem("§cOldFeast", arrayOf("", "§fLoc $location"), Material.GRASS)
                    )
                } else {
                    config.setItem(
                            8,
                            GameManager.createItem("§aFeast", arrayOf("", "§fLoc $location"), Material.BRICK)
                    )
                }

                if (Values.EVENT_MODE) {
                    config.setItem(
                            13,
                            GameManager.createItem("§aModo evento", emptyArray(), Material.LAVA_BUCKET)
                    )
                } else {
                    config.setItem(
                            13,
                            GameManager.createItem("§cModo evento", emptyArray(), Material.WATER_BUCKET)
                    )
                }

                config.setItem(27, GameManager.createItem("§aIniciar feast", emptyArray(), Material.REDSTONE_TORCH_ON))

                config.setItem(31, GameManager.createItem("§cSair", emptyArray(), Material.REDSTONE_BLOCK))

                config.setItem(35, GameManager.createItem("§aLançar mini-feast", emptyArray(), Material.TORCH))

                while (config.firstEmpty() != -1) {
                    config.setItem(
                            config.firstEmpty(),
                            GameManager.createItem("§6Config", emptyArray(), Material.STAINED_GLASS_PANE)
                    )
                }

                player.openInventory(config)

                addInventory(config)
            }

            "status" -> {
                val status = Bukkit.createInventory(player, 36, "§e§lStatus")

                var count = 0

                while (status.firstEmpty() != -1) {
                    val data = if (count % 2 == 0) {
                        0.toByte()
                    } else {
                        4.toByte()
                    }

                    status.setItem(
                            count,
                            GameManager.createItem("§a.", Material.STAINED_GLASS_PANE, data)
                    )

                    count++
                }

                val data = PlayerData.getData(player.uniqueId)

                status.setItem(12, GameManager.createItem(
                        "§aKills",
                        arrayOf(" ", "§f${data?.kills}"),
                        Material.DIAMOND_SWORD
                ))

                status.setItem(22, GameManager.createItem(
                        "§cDeaths",
                        arrayOf(" ", "§f${data?.deaths}"),
                        Material.RED_ROSE
                ))

                status.setItem(14, GameManager.createItem(
                        "§eWins",
                        arrayOf(" ", "§f${data?.wins}"),
                        Material.FIREWORK
                ))

                player.openInventory(status)

                addInventory(status)
            }

            else -> {
            }
        }
    }

    fun kitSelector(player: Player, type: Int, page: Int) {
        lateinit var inventory: Inventory

        if (type == 0) {
            inventory = Bukkit.createInventory(
                    player,
                    kitSelectorBaseInventory.size,
                    "${kitSelectorBaseInventory.title}     §fPágina §e$page"
            )

            inventory.contents = kitSelectorBaseInventory.contents
        } else {
            inventory = Bukkit.createInventory(
                    player,
                    secondaryKitSelectorBaseInventory.size,
                    "${secondaryKitSelectorBaseInventory.title}     §fPágina §e$page"
            )

            inventory.contents = secondaryKitSelectorBaseInventory.contents
        }

        val playerKits = KitManager.getPlayerKits(player, type)

        val pos = (page - 1) * 28

        if (page > 1) {
            inventory.setItem(0, GameManager.createItem("§fPágina anterior.", Material.INK_SACK, 12))

            for (i in pos until playerKits.size) {
                val kit = playerKits[i]

                if (inventory.firstEmpty() != -1) {
                    inventory.addItem(kit.getIcon())
                }
            }
        } else {
            playerKits.forEach { kit ->
                if (inventory.firstEmpty() != -1) {
                    inventory.addItem(kit.getIcon())
                }
            }
        }

        if (inventory.firstEmpty() == -1) {
            inventory.setItem(8, GameManager.createItem("§fPróxima página.", Material.INK_SACK, 10))
        }

        addInventory(inventory)

        player.openInventory(inventory)
    }

    private fun dailyKit(player: Player, inventory: Inventory) {
        Thread(Runnable {
            val task = object : BukkitRunnable() {
                val x = 20

                var i = -1

                override fun run() {
                    i++

                    if (i == x) {
                        val item = inventory.getItem(13)

                        val itemMeta = item.itemMeta
                        val name = ChatColor.stripColor(itemMeta.displayName).replace("Kit ", "").toLowerCase()

                        val kit = KitManager.getByName(name)!!

                        player.playSound(player.location, Sound.LEVEL_UP, 1.0F, 1.0F)

                        PlayerManager.addPermission(player.uniqueId, "${Permissions.KIT}${kit.getName().toLowerCase()}")

                        object : BukkitRunnable() {
                            override fun run() {
                                remove(inventory)

                                player.closeInventory()

                                player.sendMessage("${Strings.PREFIX} §fVocê ganhou o kit §a${kit.getName()} §f!")
                            }
                        }.runTaskLaterAsynchronously(RobotoHG.INSTANCE, 15L)

                        cancel()
                    } else {
                        val nonPlayerKits = KitManager.getNonPlayerKits(player, 1)

                        val kit = nonPlayerKits[(nonPlayerKits.indices).random()]

                        val icon = kit.getIcon().clone()
                        val itemMeta = icon.itemMeta
                        itemMeta.lore = emptyList()
                        icon.itemMeta = itemMeta

                        inventory.setItem(
                                13,
                                icon
                        )

                        player.playSound(player.location, Sound.ORB_PICKUP, 1.0F, 1.0F)
                    }
                }
            }

            object : BukkitRunnable() {
                var i = -1

                override fun run() {
                    i++

                    if (i != 13) {
                        if (i == inventory.size) {
                            task.runTaskTimerAsynchronously(RobotoHG.INSTANCE, 1L, 4L)
                            cancel()
                        } else {
                            val data = if (i % 2 == 0) {
                                0.toByte()
                            } else {
                                4.toByte()
                            }

                            inventory.setItem(
                                    i,
                                    GameManager.createItem("§a.", Material.STAINED_GLASS_PANE, data)
                            )
                        }
                    }

                    player.playSound(player.location, Sound.CLICK, 1.0F, 1.0F)
                }
            }.runTaskTimerAsynchronously(RobotoHG.INSTANCE, 1L, 5L)
        }).start()
    }
}