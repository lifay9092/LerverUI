package cn.lifay.ui

import java.lang.reflect.Field


/**
 * DelegateProp 委托属性（基于属性名）
 * @author lifay
 * @date 2023/3/2 21:00
 **/
class DelegateProp<T : Any, R : Any>(
    val fieldName: String,
) {
    fun getValue(obj: T): R? {
        val declaredField = getField(obj)
        val get = declaredField.get(obj)
        return get as R?
    }

    fun setValue(obj: T, v: R?) {
        val declaredField = getField(obj)
        declaredField.set(obj, v)
    }

    private fun getField(obj: T): Field {
        val declaredField = obj::class.java.getDeclaredField(fieldName)
        if (!declaredField.canAccess(obj)) {
            declaredField.isAccessible = true
        }
        return declaredField
    }

}