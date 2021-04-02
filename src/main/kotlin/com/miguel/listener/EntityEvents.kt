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

import com.miguel.game.GameStage
import com.miguel.game.Timer
import com.miguel.util.Structures
import com.miguel.util.Values
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.EntitySpawnEvent

class EntityEvents : Listener {

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        val entity = event.entity

        if (entity is Player) {
            if (Timer.gameStage != GameStage.GAME || !Values.GLOBAL_DAMAGE) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onEntitySpawn(event: EntitySpawnEvent) {
        if (event.entity is LivingEntity) {
            event.isCancelled = Timer.gameStage != GameStage.GAME
        }
    }

    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent) {
        val blockList = event.blockList()

        blockList.forEach { t ->
            if (Structures.feast.locations.contains(t.location.block))
                event.isCancelled = true
        }
    }
}