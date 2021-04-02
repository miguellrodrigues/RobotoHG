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

package com.miguel.scoreboard.major;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

/**
 * The main class of the Netherboard API for Bukkit,
 * you'll need to use it if you want to create boards.
 * <p>
 * To create a board, get the instance using <code>Netherboard.instance()</code>
 * and call one of the <code>createBoard()</code> methods.
 */
public class Netherboard {

    private static Netherboard instance;

    private final Map<Player, BPlayerBoard> boards = new HashMap<>();

    private Netherboard() {
    }

    /**
     * Returns the instance of the Netherboard class.
     *
     * @return the instance
     */
    public static Netherboard instance() {
        if (instance == null)
            instance = new Netherboard();

        return instance;
    }

    /**
     * Creates a board to a player.
     *
     * @param player the player
     * @param name   the name of the board
     * @return the newly created board
     */
    public BPlayerBoard createBoard(Player player, String name) {
        return createBoard(player, null, name);
    }

    /**
     * Creates a board to a player, using a predefined scoreboard.
     *
     * @param player     the player
     * @param scoreboard the scoreboard to use
     * @param name       the name of the board
     * @return the newly created board
     */
    public BPlayerBoard createBoard(Player player, Scoreboard scoreboard, String name) {
        deleteBoard(player);

        BPlayerBoard board = new BPlayerBoard(player, scoreboard, name);

        boards.put(player, board);
        return board;
    }

    /**
     * Deletes the board of a player.
     *
     * @param player the player
     */
    public void deleteBoard(Player player) {
        if (boards.containsKey(player))
            boards.get(player).delete();
    }

    /**
     * Removes the board of a player from the boards map.<br>
     * <b>WARNING: Do not use this to delete the board of a player!</b>
     *
     * @param player the player
     */
    public void removeBoard(Player player) {
        boards.remove(player);
    }

    /**
     * Gets the board of a player.
     *
     * @param player the player
     * @return the player board, or null if the player has no board
     */
    public BPlayerBoard getBoard(Player player) {
        return boards.get(player);
    }

}
