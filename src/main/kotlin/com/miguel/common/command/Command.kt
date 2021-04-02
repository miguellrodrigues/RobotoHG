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

package com.miguel.common.command

import com.miguel.game.GameStage

@Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER
)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class Command(
        val aliases: Array<String>,
        val usage: String = "",
        val description: String,
        val min: Int = 0,
        val max: Int = -1,
        val hidden: Boolean = false,
        val player: Boolean = true,
        val console: Boolean = true,
        val permission: Permission,
        val gameStage: Array<GameStage> = [GameStage.NONE]
)