package cn.lifay.mq.event

import cn.lifay.mq.EventBusId

/**
 *@ClassName BodyEvent
 *@Description 普通传输对象
 *@Author lifay
 *@Date 2023/8/5 16:14
 **/
class BodyEvent<T>(id: EventBusId, val body: T) : Event(id) {

}