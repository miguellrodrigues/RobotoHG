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
import com.miguel.game.GameStage
import com.miguel.game.Timer
import com.miguel.game.data.PlayerData
import com.miguel.game.kit.KitManager
import com.miguel.game.rplayer.RobotoPlayer
import com.miguel.game.rplayer.RobotoPlayerManager
import com.miguel.packet.hologram.HologramManager
import com.miguel.packet.npc.NpcManager
import com.miguel.scoreboard.ScoreboardManager
import com.miguel.scoreboard.lib.common.EntryBuilder
import com.miguel.scoreboard.lib.type.Entry
import com.miguel.scoreboard.lib.type.ScoreboardHandler
import com.miguel.util.Strings
import com.miguel.util.Structures
import com.miguel.util.Values
import io.netty.buffer.Unpooled
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.server.v1_8_R3.*
import org.bukkit.*
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.*
import org.bukkit.entity.Entity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.scheduler.BukkitRunnable
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectOutputStream
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.imageio.ImageIO
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.random.Random

object GameManager {

    val coliseum = Structures.coliseum

    private val cake = Structures.cake

    init {
        coliseum.place(Manager.getCenter(Bukkit.getWorld("world").spawnLocation))

        cake.load()
    }

    val players: MutableList<Player>
        @Synchronized
        get() {
            val lst: MutableList<Player> = ArrayList()

            Bukkit.getOnlinePlayers().forEach { player -> lst.add(player) }

            return lst
        }

    @Synchronized
    fun sendMessage(message: String) {
        players.forEach { player ->
            player.sendMessage(message)
        }
    }

    @Synchronized
    fun sendCustomMessage(message: String, command: String, hover: String) {
        val component = TextComponent(
                message
        )

        component.clickEvent = ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/hg $command"
        )
        component.hoverEvent = HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                TextComponent.fromLegacyText(hover)
        )

        players.forEach { player ->
            player.spigot().sendMessage(component)
        }
    }

    @Synchronized
    fun playSound(sound: Sound) {
        players.forEach { player ->
            player.playSound(player.location, sound, 1.0F, 1.0F)
        }
    }

    @Synchronized
    fun sendTitle(player: Player, message: String, action: PacketPlayOutTitle.EnumTitleAction) {
        val packet = PacketPlayOutTitle(
                action,
                IChatBaseComponent.ChatSerializer.a(
                        ChatColor.translateAlternateColorCodes(
                                '&',
                                "{\"text\": \"$message\"}"
                        )
                ),
                1,
                20,
                1
        )

        (player as CraftPlayer).handle.playerConnection.sendPacket(packet)
    }

    @Synchronized
    fun sendActionBar(player: Player, message: String) {
        val packet = PacketPlayOutChat(
                IChatBaseComponent.ChatSerializer.a(
                        ChatColor.translateAlternateColorCodes(
                                '&',
                                "{\"text\": \"$message\"}"
                        )
                ), 2
        )

        (player as CraftPlayer).handle.playerConnection.sendPacket(packet)
    }

    @Synchronized
    private fun sendTab(player: Player, head: String, foot: String) {
        val header = IChatBaseComponent.ChatSerializer.a("{\"text\": \"$head\"}")
        val footer = IChatBaseComponent.ChatSerializer.a("{\"text\": \"$foot\"}")

        val packet = PacketPlayOutPlayerListHeaderFooter(header)

        val footerField = packet.javaClass.getDeclaredField("b")
        footerField.isAccessible = true
        footerField.set(packet, footer)

        (player as CraftPlayer).handle.playerConnection.sendPacket(packet)
    }

    @Synchronized
    fun name(i: ItemStack): String {
        val name: String

        when (i.type) {
            Material.AIR -> name = "Punho"
            Material.WOOD_SWORD -> name = "Espada de Madeira"
            Material.STONE_SWORD -> name = "Espada de Pedra"
            Material.IRON_SWORD -> name = "Espada de Ferro"
            Material.DIAMOND_SWORD -> name = "Espada de Diamante"
            Material.GOLD_SWORD -> name = "Espada de Ouro"
            Material.WOOD_AXE -> name = "Machado de Madeira"
            Material.STONE_AXE -> name = "Machado de Pedra"
            Material.IRON_AXE -> name = "Machado de Ferro"
            Material.DIAMOND_AXE -> name = "Machado de Diamante"
            Material.GOLD_AXE -> name = "Machado de Ouro"
            Material.WOOD_SPADE -> name = "Pá de Madeira"
            Material.STONE_SPADE -> name = "Pá de Pedra"
            Material.IRON_SPADE -> name = "Pa de Ferro"
            Material.DIAMOND_SPADE -> name = "Pá de Diamante"
            Material.GOLD_SPADE -> name = "Pá de Ouro"
            Material.WOOD_PICKAXE -> name = "Picareta de Madeira"
            Material.STONE_PICKAXE -> name = "Picareta de Pedra"
            Material.IRON_PICKAXE -> name = "Picareta de Ferro"
            Material.DIAMOND_PICKAXE -> name = "Picareta de Diamante"
            Material.GOLD_PICKAXE -> name = "Picareta de Ouro"
            Material.STICK -> name = "Graveto"
            Material.MUSHROOM_SOUP -> name = "Sopa"
            Material.BOWL -> name = "Tigela"
            Material.SPONGE -> name = "Launcher"
            Material.DIRT -> name = "Terra"
            Material.COMPASS -> name = "Bússola"

            else -> {
                name = if ((i.hasItemMeta()) && (i.itemMeta.hasDisplayName()))
                    ChatColor.stripColor(i.itemMeta.displayName)
                else if (i.type.toString().length < 16)
                    i.type.toString()
                else
                    i.type.toString().substring(0, 15)
            }
        }

        return name
    }

    @Synchronized
    fun getDeathCause(player: Player): String {
        val lastDamageCause = player.lastDamageCause

        val msg = StringBuilder("§fO jogador §a${player.name} ${KitManager.getKitsName(player)} §f")

        if (player.killer != null) {
            val killer = player.killer

            msg.append("Foi morto por §a${killer.name} ${KitManager.getKitsName(killer)} §fUsando um'a §a${name(killer.itemInHand)}")
        } else {
            if (lastDamageCause != null) {
                if (lastDamageCause is EntityDamageByEntityEvent) {
                    val entity: Entity = lastDamageCause.damager

                    msg.append(
                            when (entity.type) {
                                EntityType.PRIMED_TNT -> "morreu para uma TNT"
                                EntityType.CREEPER -> "morreu para um Creeper"
                                EntityType.SKELETON -> "morreu para um Esqueleto"
                                EntityType.SPIDER -> "morreu para uma Aranha"
                                EntityType.ZOMBIE -> "morreu para um Zumbi"
                                EntityType.ENDERMAN -> "morreu para um Enderman"
                                EntityType.CAVE_SPIDER -> "morreu para uma Aranha da caverna"
                                EntityType.WITCH -> "morreu para uma Bruxa"
                                EntityType.WOLF -> "morreu para um Lobo raivoso"

                                else -> {
                                    "morreu por um motivo desconhecido"
                                }
                            }
                    )

                    if (entity is Projectile) {
                        val shooter = entity.shooter

                        if (shooter is Player) {
                            if (player.killer == null) {
                                msg.append("morreu para uma flechada de §a" + shooter.name)
                            }
                        } else if (shooter is Skeleton) {
                            msg.append("morreu para um flechada de esqueleto")
                        }
                    }
                } else {
                    when (lastDamageCause.cause) {
                        EntityDamageEvent.DamageCause.CONTACT -> msg.append("morreu para um cacto")
                        EntityDamageEvent.DamageCause.ENTITY_ATTACK -> msg.append("morreu para uma horda de monstros")
                        EntityDamageEvent.DamageCause.PROJECTILE -> msg.append("morreu por um prójetil")
                        EntityDamageEvent.DamageCause.SUFFOCATION -> msg.append("morreu sufocado")
                        EntityDamageEvent.DamageCause.FALL -> msg.append("morreu de queda")
                        EntityDamageEvent.DamageCause.FIRE -> msg.append("morreu pegando fogo")
                        EntityDamageEvent.DamageCause.FIRE_TICK -> msg.append("morreu pegando fogo")
                        EntityDamageEvent.DamageCause.MELTING -> msg.append("morreu derretido")
                        EntityDamageEvent.DamageCause.LAVA -> msg.append("morreu na lava")
                        EntityDamageEvent.DamageCause.DROWNING -> msg.append("morreu afogado")
                        EntityDamageEvent.DamageCause.BLOCK_EXPLOSION -> msg.append("morreu explodido")
                        EntityDamageEvent.DamageCause.ENTITY_EXPLOSION -> msg.append("morreu explodido")
                        EntityDamageEvent.DamageCause.VOID -> msg.append("morreu no void")
                        EntityDamageEvent.DamageCause.LIGHTNING -> msg.append("morreu atingido por um raio")
                        EntityDamageEvent.DamageCause.SUICIDE -> msg.append("suicidou")
                        EntityDamageEvent.DamageCause.STARVATION -> msg.append("morreu de fome")
                        EntityDamageEvent.DamageCause.POISON -> msg.append("morreu envenenado")
                        EntityDamageEvent.DamageCause.MAGIC -> msg.append("morreu para magia")
                        EntityDamageEvent.DamageCause.WITHER -> msg.append("morreu para podridão")
                        EntityDamageEvent.DamageCause.FALLING_BLOCK -> msg.append("morreu esmagado")
                        EntityDamageEvent.DamageCause.THORNS -> msg.append("morreu para espinhos na armadura")
                        EntityDamageEvent.DamageCause.CUSTOM -> msg.append("morreu por um motivo desconhecido")

                        else -> {
                            msg.append("morreu por um motivo desconhecido")
                        }
                    }
                }
            }
        }

        return msg.toString()
    }

    @Synchronized
    fun setScoreboard(player: Player): ScoreboardManager {
        val scoreboardManager = ScoreboardManager(player)

        scoreboardManager.setHandler(object : ScoreboardHandler {
            lateinit var robotoPlayer: RobotoPlayer

            val formatter = DateTimeFormatter.ofPattern("dd MM yyyy HH:mm")

            override fun getTitle(player: Player?): String {
                return Strings.SERVER_NAME
            }

            override fun getEntries(player: Player): MutableList<Entry> {
                robotoPlayer = RobotoPlayerManager.getrobotoPlayer(player)
                val today = LocalDateTime.now()

                val entryBuilder = EntryBuilder()

                entryBuilder.next("        §7www.Robotohg.com.br")
                entryBuilder.next("§f§m ▀ ▀ ▀ ▀ ▀ ▀ ▀ ▀ ▀ ▀")
                entryBuilder.blank()

                entryBuilder.next("§fJogadores §e${RobotoPlayerManager.getPlaying().size}")
                entryBuilder.next("§fTempo §e${Manager.formatTime(Timer.gameTime)}")
                entryBuilder.blank()
                entryBuilder.next("§fKit 1 §e${robotoPlayer.primaryKit.getName()}")
                entryBuilder.next("§fKit 2 §e${robotoPlayer.secondaryKit.getName()}")
                entryBuilder.blank()
                entryBuilder.next("§fKills §e")
                entryBuilder.next("§fData §e${formatter.format(today)}")
                entryBuilder.blank()

                return entryBuilder.build()
            }
        }).setDelay(10L).build()

        return scoreboardManager
    }

    @Synchronized
    fun createItem(name: String, description: Array<String>, type: Material): ItemStack {
        val stack = ItemStack(type)

        val itemMeta = stack.itemMeta
        itemMeta.displayName = name
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)

        if (description.isNotEmpty())
            itemMeta.lore = description.toMutableList()

        stack.itemMeta = itemMeta

        return stack
    }

    @Synchronized
    fun createItem(name: String, type: Material, data: Byte): ItemStack {
        val stack = ItemStack(type)

        if (data >= 0) {
            stack.data = type.getNewData(data)
            stack.durability = data.toShort()
        }

        val itemMeta = stack.itemMeta
        itemMeta.displayName = name
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)

        stack.itemMeta = itemMeta

        return stack
    }

    @Synchronized
    private fun createHead(name: String): ItemStack {
        val head = ItemStack(Material.SKULL_ITEM, 1, 3.toShort())

        val itemMeta = head.itemMeta as SkullMeta
        itemMeta.owner = name
        itemMeta.displayName = "§eStatus"

        head.itemMeta = itemMeta

        return head
    }

    @Synchronized
    private fun giveInitialItems(player: Player) {
        val kitSelector = createItem("§eKit Selector", arrayOf(""), Material.STORAGE_MINECART)
        val dailyKit = createItem("§eKit diário", arrayOf(""), Material.ENDER_CHEST)
        val kitShop = createItem("§eShop", arrayOf(""), Material.GOLD_INGOT)
        val biomes = createItem("§eBiomas", arrayOf(""), Material.BOOK)

        val status = createHead(player.name)

        player.inventory.setItem(0, kitSelector)
        player.inventory.setItem(3, dailyKit)
        player.inventory.setItem(4, biomes)
        player.inventory.setItem(5, kitShop)
        player.inventory.setItem(8, status)
    }

    @Synchronized
    fun init(player: Player) {
        if (Timer.gameStage == GameStage.PREGAME) {
            object : BukkitRunnable() {
                override fun run() {
                    sendTab(
                            player,
                            "${Strings.SERVER_NAME} \n      §7Servidor §fRoboto Source \n §7Você está conectado a sala: §f1\n",
                            "\n§7Tenha um bom jogo!"
                    )

                    sendTitle(
                            player,
                            Strings.SERVER_NAME,
                            PacketPlayOutTitle.EnumTitleAction.TITLE
                    )

                    sendTitle(
                            player,
                            "§fSeja bem vindo ${player.name}",
                            PacketPlayOutTitle.EnumTitleAction.SUBTITLE
                    )
                }
            }.runTaskLaterAsynchronously(RobotoHG.INSTANCE, 10L)

            player.inventory.clear()
            player.inventory.armorContents = null

            giveInitialItems(player)

            player.teleport(Manager.getCenter(player.world.spawnLocation.clone().add(0.0, 1.0, 0.0)))

            player.gameMode = GameMode.ADVENTURE
        }

        val robotoPlayer = RobotoPlayerManager.getrobotoPlayer(player)

        robotoPlayer.scoreboardManager = setScoreboard(player)

        sendCustomPayload(player)
    }

    @Synchronized
    private fun sendCustomPayload(player: Player) {
        try {
            val payloadMap = HashMap<String, Boolean>()

            payloadMap["IMPROVED_LAVA"] = true
            payloadMap["REFILL_FIX"] = true
            payloadMap["CROSSHAIR_SYNC"] = true

            payloadMap["DAMAGEINDICATOR"] = false

            val byteOut = ByteArrayOutputStream()
            val out = ObjectOutputStream(byteOut)
            out.writeObject(payloadMap)

            val byteBuffer = Unpooled.copiedBuffer(byteOut.toByteArray())
            val packetData = PacketDataSerializer(byteBuffer)

            val packet = PacketPlayOutCustomPayload("LABYMOD", packetData)

            (player as CraftPlayer).handle.playerConnection.sendPacket(packet)
        } catch (e: IOException) {
            throw Error(e.message)
        }
    }

    fun worldBorder() {
        val world = Bukkit.getWorld("world")

        val worldBorder = world.worldBorder

        worldBorder.center = Location(world, 0.0, 0.0, 0.0)
        worldBorder.size = 1000.0
        worldBorder.warningDistance = 15

        /*for (x in 0..500) {
            for (z in 0..500) {
                if (x % 16 == 0 && z % 16 == 0) {
                    val location = Location(
                        world,
                        x.toDouble(),
                        world.getHighestBlockYAt(x, z).toDouble(),
                        z.toDouble()
                    )

                    location.chunk.load(true)
                }
            }
        }*/
    }

    private fun grace() {
        val playing = RobotoPlayerManager.getPlaying()

        if (playing.isEmpty())
            return

        val bestowed = playing[(0 until playing.size).random()].player

        val stack = ItemStack(Values.graceItem[(Values.graceItem.indices).random()])

        bestowed.inventory.addItem(stack)

        sendMessage("${Strings.PREFIX} O jogador §a${bestowed.name} §fRecebeu a benção§e!")
    }

    fun invencibility() {
        playSound(Sound.ANVIL_USE)
        sendMessage("${Strings.PREFIX} §fA partida iniciou§e!")

        coliseum.locations.forEach { block ->
            if (block.type.name.contains("PISTON"))
                block.type = Material.AIR
        }

        HologramManager.destroyAll()

        players.forEach { player ->
            val robotoPlayer = RobotoPlayerManager.getrobotoPlayer(player)

            if (robotoPlayer.isPlaying()) {
                player.inventory.clear()
                player.inventory.armorContents = null

                player.gameMode = GameMode.SURVIVAL

                KitManager.giveItems(player)
            }
        }

        NpcManager.destroyAll()
    }

    fun game() {
        RobotoPlayerManager.getAll().forEach { oplayer ->
            oplayer.primaryKit.canUse = true
            oplayer.secondaryKit.canUse = true
        }

        playSound(Sound.ANVIL_BREAK)
        sendMessage("${Strings.PREFIX} §fA invencibilidade acabou§e!")

        grace()
    }

    fun win() {
        if (RobotoPlayerManager.getPlaying().size == 1) {
            Timer.start(GameStage.WIN, Timer.gameTime)
        }
    }

    fun win(player: Player) {
        player.inventory.clear()
        player.inventory.armorContents = null

        player.inventory.setItem(0, ItemStack(Material.MAP))
        player.inventory.setItem(8, ItemStack(Material.WATER_BUCKET))

        val add = Manager.getCenter(player.location.clone().add(0.0, 50.0, 0.0))

        cake.placeLoaded(add)

        player.teleport(add.add(0.0, 1.5, 0.0))

        PlayerData.getData(player.uniqueId)!!.wins += 1
    }

    @Synchronized
    fun fillChest(chest: Chest, structure: String) {
        when (structure) {
            "feast" -> {
                Values.feastItems.forEach { material ->
                    val stack = ItemStack(material)
                    if (material.name.contains("DIAMOND")) {
                        if (Random.nextDouble() > 0.8) {
                            chest.inventory.setItem((0 until chest.inventory.size - 1).random(), stack)
                        }
                    } else if (material.name.contains("IRON")) {
                        if (Random.nextDouble() > 0.8) {
                            chest.inventory.setItem((0 until chest.inventory.size - 1).random(), stack)
                        }
                    } else {
                        stack.amount = (1..3).random()
                        chest.inventory.setItem((0 until chest.inventory.size - 1).random(), stack)
                    }
                }
            }

            "minifeast" -> {
                Values.miniFeastItems.forEach { material ->
                    val stack = ItemStack(material)
                    if (material.name.contains("IRON")) {
                        if (Random.nextDouble() > 0.4) {
                            chest.inventory.setItem((0 until chest.inventory.size - 1).random(), stack)
                        }
                    } else {
                        stack.amount = (1..3).random()
                        chest.inventory.setItem((0 until chest.inventory.size - 1).random(), stack)
                    }
                }
            }

            else -> {
            }
        }
    }

    fun addCustomRecipes() {
        val soup = createItem("§eSopa", arrayOf(" ", "§fUse para regenerar sua §cvida"), Material.MUSHROOM_SOUP)

        val mushroomSoup = ShapelessRecipe(soup)
        mushroomSoup.addIngredient(1, Material.BROWN_MUSHROOM)
        mushroomSoup.addIngredient(1, Material.RED_MUSHROOM)
        mushroomSoup.addIngredient(1, Material.BOWL)

        val flowerSoup = ShapelessRecipe(soup)
        flowerSoup.addIngredient(1, Material.RED_ROSE)
        flowerSoup.addIngredient(1, Material.YELLOW_FLOWER)
        flowerSoup.addIngredient(1, Material.BOWL)

        val cactusSoup = ShapelessRecipe(soup)
        cactusSoup.addIngredient(1, Material.CACTUS)
        cactusSoup.addIngredient(1, Material.BOWL)

        val cocoaSoup = ShapelessRecipe(soup)
        cocoaSoup.addIngredient(1, Material.COCOA)
        cocoaSoup.addIngredient(1, Material.BOWL)

        val ironRecipe = ShapedRecipe(ItemStack(Material.IRON_INGOT, 8))
        ironRecipe.shape("III", "ICI", "III")
        ironRecipe.setIngredient('I', Material.IRON_ORE)
        ironRecipe.setIngredient('C', Material.COAL)

        Bukkit.addRecipe(mushroomSoup)

        Bukkit.addRecipe(flowerSoup)
        Bukkit.addRecipe(cactusSoup)
        Bukkit.addRecipe(cocoaSoup)

        Bukkit.addRecipe(ironRecipe)
    }

    fun firework(location: Location, color: Color, boo: Boolean) {
        val fireWork = location.world.spawnEntity(location, EntityType.FIREWORK) as Firework
        val meta = fireWork.fireworkMeta

        if (boo) {
            meta.addEffect(
                    FireworkEffect.builder().trail(true).flicker(true).withColor(color).with(FireworkEffect.Type.CREEPER)
                            .build()
            )
        } else {
            meta.addEffect(
                    FireworkEffect.builder().trail(true).flicker(true).withColor(color).with(FireworkEffect.Type.BURST)
                            .build()
            )
        }

        meta.power = 1
        fireWork.fireworkMeta = meta

        fireWork.detonate()
    }

    @Synchronized
    fun getIcon(player: Player): BufferedImage {
        val url = URL("https://minotar.net/helm/${player.name}/64")

        return ImageIO.read(url)
    }

    @Synchronized
    fun canDailyKit(player: Player): Int {
        val date = Date()

        val dailyKit = PlayerData.getData(player.uniqueId)!!.dailyKit

        val playerKits = KitManager.getNonPlayerKits(player, 1)

        if (playerKits.isNotEmpty()) {
            if (date.after(dailyKit)) {
                return 1
            }
        } else {
            return 2
        }

        return 0
    }
}