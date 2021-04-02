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

package com.miguel.scoreboard.lib.type;

import org.bukkit.entity.Player;

import java.util.List;

public interface ScoreboardHandler {

    String getTitle(Player player);

    List<Entry> getEntries(Player player);
}
