package cn.lifay.mq

import cn.lifay.extension.asyncTask
import cn.lifay.mq.event.Event
import cn.lifay.ui.BaseView
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.jvm.internal.FunctionReferenceImpl
import kotlin.reflect.KClass
import kotlin.reflect.KFunction2

object FXEventBusOpt {

    private val RECEIVER_MAP = HashMap<String, ArrayList<Pair<BaseView<*>, KFunction2<Any, Array<Event>, Any>>>>()
    private val CLASS_METHOD_LIST = ArrayList<String>()

    /*
        订阅者列表 K=事件类型 V=回调函数列表
     */
    val SUBSCRIBERS_TYPE = ConcurrentHashMap<String, KClass<out Event>>()
    val SUBSCRIBERS_FUNC = ConcurrentHashMap<String, CopyOnWriteArraySet<(Event) -> Unit>>()
    fun <T : Event> subscribe(id: String, eventType: KClass<T>, subscriber: (T) -> Unit) {
        Objects.requireNonNull(id)
        Objects.requireNonNull(eventType)
        Objects.requireNonNull(subscriber)

        if (!SUBSCRIBERS_TYPE.containsKey(id)) {
            val eventFuncs = CopyOnWriteArraySet<(Event) -> Unit>()
            eventFuncs!!.add(subscriber as ((Event) -> Unit)?)
            SUBSCRIBERS_TYPE[id] = eventType
            SUBSCRIBERS_FUNC[id] = eventFuncs
        } else {
            val eventFuncs = SUBSCRIBERS_FUNC[id]
            eventFuncs!!.add(subscriber as ((Event) -> Unit)?)
        }

    }

    fun <T : Event> publish(event: T) {
        Objects.requireNonNull(event)
        if (SUBSCRIBERS_TYPE.containsKey(event.id)) {
            val eventType = SUBSCRIBERS_TYPE[event.id]
            if (eventType != event::class) {
                throw FXEventBusException("event class 不一致！ subscribers:${eventType} publish:${event::class}")
            }
            val eventFuncs = SUBSCRIBERS_FUNC[event.id]
            eventFuncs?.forEach {
                asyncTask {
                    it.invoke(event)
                }
            }
        }
    }


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

    fun <T : Event> invoke(id: String, body: T) {
        val receivers = RECEIVER_MAP[id]
        receivers!!.forEach {
            it.second(it.first, arrayOf(body))
        }

    }

}