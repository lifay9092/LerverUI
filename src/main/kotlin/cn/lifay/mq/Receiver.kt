package cn.lifay.mq

import cn.lifay.mq.event.Event
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
annotation class Receiver<T : Event>(
    val id: String,
    val body: KClass<T>,
//    val result : KClass<*> = Unit::class
)
