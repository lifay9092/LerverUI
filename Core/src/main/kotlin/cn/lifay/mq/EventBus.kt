package cn.lifay.mq

import cn.lifay.extension.asyncTask
import cn.lifay.mq.event.Event
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.reflect.KClass
import kotlin.reflect.jvm.reflect

object EventBus {
/*

    private val RECEIVER_MAP = HashMap<String, ArrayList<Pair<BaseView<*>, KFunction2<Any, Array<Event>, Any>>>>()
    private val CLASS_METHOD_LIST = ArrayList<String>()
*/


    /*
        订阅者列表 K=事件类型 V=回调函数列表
     */
    val SUBSCRIBERS_TYPE = ConcurrentHashMap<EventBusId, KClass<out Event>>()
    val SUBSCRIBERS_FUNC = ConcurrentHashMap<EventBusId, CopyOnWriteArraySet<(Event) -> Unit>>()

    /**
     * 订阅注册:,指定id,Event传参class,回调函数
     * EventBus.subscribe("chatAppendText", TextEvent::class) {
     *      platformRun {
     *           textArea.appendText("${it.text}\n")
     *      }
     * }
     * @author lifay
     */
    fun <T : Event> subscribe(id: EventBusId, eventType: KClass<T>, subscriber: (T) -> Unit) {
        Objects.requireNonNull(id)
        Objects.requireNonNull(eventType)
        Objects.requireNonNull(subscriber)

        println("func:${subscriber.hashCode()}")
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

    /**
     * 发布
     * EventBus.publish(TextEvent("chatAppendText", textArea.text))
     * @param event 事件体
     * @author lifay
     */
    fun <T : Event> publish(event: T) {
        Objects.requireNonNull(event)
        if (SUBSCRIBERS_TYPE.containsKey(event.id)) {
            val eventType = SUBSCRIBERS_TYPE[event.id]
            if (eventType != event::class) {
                throw EventBusException("event class 不一致！ subscribers:${eventType} publish:${event::class}")
            }
            val eventFuncs = SUBSCRIBERS_FUNC[event.id]
            println(eventFuncs?.size)
            eventFuncs?.forEach {
                asyncTask {
                    println(it.reflect())
                    println(it(event))
                }
            }
        }
    }

    /**
     * 取消订阅
     * EventBus.unSubscribe("chatAppendText")
     * @param event 事件体
     * @author lifay
     */
    fun unSubscribe(id: EventBusId) {
        Objects.requireNonNull(id)
        if (SUBSCRIBERS_TYPE.containsKey(id)) {
            SUBSCRIBERS_TYPE.remove(id)
            SUBSCRIBERS_FUNC.remove(id)
        }
    }

}