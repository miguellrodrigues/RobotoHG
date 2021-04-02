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

public class StaticString implements AnimatableString {

    private final String string;

    public StaticString(String string) {
        this.string = string;
    }

    @Override
    public String current() {
        return string;
    }

    @Override
    public String previous() {
        return string;
    }

    @Override
    public String next() {
        return string;
    }

}
