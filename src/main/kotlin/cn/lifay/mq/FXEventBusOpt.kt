package cn.lifay.mq

import cn.lifay.ui.BaseView
import java.lang.reflect.Method
import kotlin.jvm.internal.FunctionReferenceImpl
import kotlin.reflect.KFunction2

object FXEventBusOpt {

    private val RECEIVER_MAP = HashMap<String, ArrayList<Pair<BaseView<*>, KFunction2<Any, Array<out Any>, Any>>>>()
    private val CLASS_METHOD_LIST = ArrayList<String>()
    fun add(id: String, obj: BaseView<*>, f: KFunction2<Any, Array<out Any>, Any>) {
        var receivers = RECEIVER_MAP[id]
        if (receivers == null) {
            receivers = ArrayList()
        }
        receivers.add(Pair(obj, f))
        val referenceImpl = f as FunctionReferenceImpl
        val method = referenceImpl.boundReceiver as Method
//            println("f:" + method.name)
        RECEIVER_MAP[id] = receivers
        CLASS_METHOD_LIST.add("${obj.javaClass.name}_${method.name}")
    }

    fun has(clazzName: String, methodName: String): Boolean {
        return CLASS_METHOD_LIST.contains("${clazzName}_${methodName}")
    }

    fun invoke(id: String, args: Array<out Any>) {
        val receivers = RECEIVER_MAP[id]
        receivers!!.forEach {
            it.second(it.first, args)
        }
    }

}