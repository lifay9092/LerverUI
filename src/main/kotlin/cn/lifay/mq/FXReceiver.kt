package cn.lifay.mq

@Target(AnnotationTarget.FUNCTION)
annotation class FXReceiver(
    val id: String
)
