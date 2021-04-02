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

/**
 * Represents an advanced scoreboard that can display up to 48 characters in a single entry.
 *
 * @author TigerHix
 */
public interface Scoreboard {

    /**
     * Activate the scoreboard.
     */
    void activate();

    /**
     * Deactivate the scoreboard.
     */
    void deactivate();

    /**
     * Determine if the scoreboard has been already activated.
     *
     * @return activated
     */
    boolean isActivated();

    /**
     * Returns the handler for this scoreboard.
     *
     * @return handler
     */
    ScoreboardHandler getHandler();

    /**
     * Set the handler for this scoreboard.
     *
     * @param handler handler
     */
    Scoreboard setHandler(ScoreboardHandler handler);

    /**
     * Returns the updateDisplayName interval (default = 10L).
     *
     * @return updateDisplayName interval
     */
    long getUpdateInterval();

    /**
     * Set the updateDisplayName interval.
     *
     * @param updateInterval updateDisplayName interval
     * @return this
     */
    Scoreboard setUpdateInterval(long updateInterval);

    /**
     * Returns the holder of this scoreboard.
     *
     * @return holder
     */
    Player getHolder();

}
