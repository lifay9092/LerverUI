package cn.lifay.mq.event

/**
 *@ClassName TextEvent
 *@Description 普通传输文本
 *@Author lifay
 *@Date 2023/8/5 16:14
 **/
class TextEvent(id: String, val text: String) : Event(id) {

}