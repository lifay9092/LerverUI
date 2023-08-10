package cn.lifay.ui

import kotlin.reflect.KClass


/**
 * DelegateProp 委托属性（基于属性名）
 * @author lifay
 * @date 2023/3/2 21:00
 **/
class DelegateProp<T : Any, R : Any>(
    val fieldName: String,
    val kClass: KClass<T>,
    val rClass: KClass<R>
) {

    companion object {
        inline operator fun <reified T : Any, reified R : Any> invoke(fieldName: String) =
            DelegateProp(fieldName, T::class, R::class)

    }

    fun getValue(obj: T): R? {
        val declaredField = obj!!::class.java.getDeclaredField(fieldName)
        declaredField.isAccessible = true
        val get = declaredField.get(obj)
        println("反射值:$get")
        return get as R?
    }

    fun setValue(obj: T, v: R?) {
        val declaredField = obj!!::class.java.getDeclaredField(fieldName)
        declaredField.isAccessible = true
        declaredField.set(obj, v)
    }

}