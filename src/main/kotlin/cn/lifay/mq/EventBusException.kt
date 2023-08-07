package cn.lifay.mq


/**
 * FXEventBusException TODO
 * @author lifay
 * @date 2023/2/24 11:17
 **/
class EventBusException(message: String?) : RuntimeException(message) {

    override val message: String
        get() = "EventBusException error : ${super.message}"
}