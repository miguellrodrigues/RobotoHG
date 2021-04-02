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

package com.miguel.util

import com.miguel.structures.StructureManager

object Structures {

    lateinit var coliseum: StructureManager.Structure

    lateinit var feast: StructureManager.Structure
    lateinit var miniFeast: StructureManager.Structure

    lateinit var cake: StructureManager.Structure

    fun load() {
        coliseum = StructureManager.get("coliseum")
        feast = StructureManager.get("feast")
        miniFeast = StructureManager.get("mini-feast")
        cake = StructureManager.get("cake")
    }
}