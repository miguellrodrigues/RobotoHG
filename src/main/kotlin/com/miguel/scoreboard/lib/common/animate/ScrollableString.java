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

package com.miguel.scoreboard.lib.common.animate;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ScrollableString extends FrameAnimatedString {

    private final List<String> list;
    private int position;
    private ChatColor color = ChatColor.GREEN;

    public ScrollableString(String message, int width, int spaceBetween) {
        list = new ArrayList<>();
        // String is too short for window?
        if (message.length() < width) {
            StringBuilder sb = new StringBuilder(message);
            while (sb.length() < width)
                sb.append(" ");
            message = sb.toString();
        }
        // Allow for colours which add 2 to the width
        width -= 2;
        // Invalid width/space size
        if (width < 1)
            width = 1;
        if (spaceBetween < 0)
            spaceBetween = 0;
        // Add substrings
        for (int i = 0; i < message.length() - width; i++)
            list.add(message.substring(i, i + width));
        // Add space between repeats
        StringBuilder space = new StringBuilder();
        for (int i = 0; i < spaceBetween; ++i) {
            list.add(message.substring(message.length() - width + (Math.min(i, width))) + space);
            if (space.length() < width)
                space.append(" ");
        }
        // Wrap
        for (int i = 0; i < width - spaceBetween; ++i)
            list.add(message.substring(message.length() - width + spaceBetween + i) + space + message.substring(0, i));
        // Join up
        for (int i = 0; i < spaceBetween; i++) {
            if (i > space.length())
                break;
            list.add(space.substring(0, space.length() - i) + message.substring(0, width - (spaceBetween > width ? width : spaceBetween) + i));
        }
    }

    @Override
    public String next() {
        StringBuilder sb = getNext();
        if (sb.charAt(sb.length() - 1) == ChatColor.COLOR_CHAR) {
            sb.setCharAt(sb.length() - 1, ' ');
        }
        if (sb.charAt(0) == ChatColor.COLOR_CHAR) {
            ChatColor c = ChatColor.getByChar(sb.charAt(1));
            if (c != null) {
                color = c;
                sb = getNext();
                if (sb.charAt(0) != ' ')
                    sb.setCharAt(0, ' ');
            }
        }
        return color + sb.toString();
    }

    private StringBuilder getNext() {
        return new StringBuilder(list.get(position++ % list.size()));
    }
}
