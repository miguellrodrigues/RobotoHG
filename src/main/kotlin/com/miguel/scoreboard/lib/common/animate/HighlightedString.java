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

public class HighlightedString extends FrameAnimatedString {

    protected String context;
    protected String normalFormat;
    protected String highlightFormat;
    protected String prefix = "";
    protected String suffix = "";

    public HighlightedString(String context, String normalFormat, String highlightFormat) {
        super();
        this.context = context;
        this.normalFormat = normalFormat;
        this.highlightFormat = highlightFormat;
        generateFrames();
    }

    public HighlightedString(String context, String normalFormat, String highlightFormat, String prefix, String suffix) {
        super();
        this.context = context;
        this.normalFormat = normalFormat;
        this.highlightFormat = highlightFormat;
        this.prefix = prefix;
        this.suffix = suffix;
        generateFrames();
    }

    protected void generateFrames() {
        int index = 0;
        while (index < context.length()) {
            if (context.charAt(index) != ' ') {
                String highlighted = normalFormat + context.substring(0, index) + highlightFormat + context.charAt(index) + normalFormat + context.substring(index + 1);
                String whole = prefix + highlighted + suffix;
                addFrame(whole);
            } else {
                addFrame(prefix + normalFormat + context + suffix);
            }
            index++;
        }
    }

    public String getContext() {
        return context;
    }

    public String getNormalColor() {
        return normalFormat;
    }

    public String getHighlightColor() {
        return highlightFormat;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

}
