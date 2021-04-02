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

package com.miguel.scoreboard.lib.common;

import com.miguel.scoreboard.lib.type.Entry;

import java.util.LinkedList;
import java.util.List;

/**
 * An utility to make pretty entries for the scoreboards, without calculating the positions by yourself.
 *
 * @author TigerHix
 */
public final class EntryBuilder {

    private final LinkedList<Entry> entries = new LinkedList<>();
    private int spaces = 0;

    /**
     * Append a blank line.
     *
     * @return this
     */
    public EntryBuilder blank() {
        String line = "";
        for (int i = 0; i <= spaces; i++) {
            line += " ";
        }
        spaces += 1;
        return next(line);
    }

    /**
     * Append a new line with specified text.
     *
     * @param string text
     * @return this
     */
    public EntryBuilder next(String string) {
        entries.add(new Entry(adapt(string), entries.size()));
        return this;
    }

    /**
     * Returns a map of entries.
     *
     * @return map
     */
    public List<Entry> build() {
        for (Entry entry : entries) {
            entry.setPosition(entries.size() - entry.getPosition());
        }
        return entries;
    }

    private String adapt(String entry) {
        // Cut off the exceeded part if needed
        if (entry.length() > 48) entry = entry.substring(0, 47);
        return Strings.format(entry);
    }

}
