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

package com.miguel.scoreboard;

import com.miguel.scoreboard.lib.type.Entry;
import org.bukkit.entity.Player;

import java.util.List;

public interface Handler {
    /**
     * Determines the title to display for this player. If null returned, title automatically becomes a blank line.
     *
     * @param player player
     * @return title
     */
    String getTitle(Player player);

    /**
     * Determines the entries to display for this player. If null returned, the entries are not updated.
     *
     * @param player player
     * @return entries
     */
    List<Entry> getEntries(Player player);
}