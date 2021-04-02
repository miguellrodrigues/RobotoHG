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

package com.miguel.reflection

import java.lang.reflect.Field

object NMSUtil {

    fun getValue(obj: Any, name: String): Any? {
        try {
            val field: Field = obj.javaClass.getDeclaredField(name)
            field.isAccessible = true
            return field.get(obj)
        } catch (e: Exception) {
            throw Error(e.message)
        }
    }

    fun setValue(instance: Any, field: String, value: Any) {
        try {
            val f = instance.javaClass.getDeclaredField(field)
            f.isAccessible = true
            f[instance] = value
        } catch (e: Exception) {
            throw Error(e.message)
        }
    }
}