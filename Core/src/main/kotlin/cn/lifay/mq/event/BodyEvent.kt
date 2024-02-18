package cn.lifay.mq.event

/**
 *@ClassName BodyEvent
 *@Description 普通传输对象
 *@Author lifay
 *@Date 2023/8/5 16:14
 **/
class BodyEvent<T>(id: String, val body: T) : Event(id) {

}