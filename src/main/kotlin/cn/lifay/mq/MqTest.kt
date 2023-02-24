package cn.lifay.mq

class MqTest() {

    fun test(vararg args: Any) {

    }
}

fun main() {
    val mqTest = MqTest()
    a(mqTest::test)
}

fun a(kFunction0: () -> Unit) {

}