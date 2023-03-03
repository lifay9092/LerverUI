package cn.lifay.ui.form.radio

import cn.lifay.ui.DelegateProp
import cn.lifay.ui.form.FormElement
import javafx.scene.Node
import kotlin.reflect.KMutableProperty1


/**
 * RadioElement 选择组-元素
 * @author lifay
 * @date 2023/2/6 14:47
 **/
class RadioElement<T : Any, R : Any>(
    r: Class<R>,
    label: String,
    property: KMutableProperty1<T, R?>?,
    customProp: DelegateProp<T, R>? = null,
    val items: List<R>
) : FormElement<T, R>(r, label, property, customProp) {

    /*  constructor(label: String,
                  property: KMutableProperty1<T, R?>,
                  items: List<String>):this(r,label,property,null,items)

      constructor(label: String,
                  customProp : DelegateProp<T, String>,
                  items: List<String>):this(label,null,customProp,items)*/
    companion object {
        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            property: KMutableProperty1<T, R?>,
            items: List<R>
        ) = RadioElement(R::class.java, label, property, null, items)

        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            customProp: DelegateProp<T, R>,
            items: List<R>
        ) = RadioElement(R::class.java, label, null, customProp, items)
    }

    init {
//        this.items = items
        graphic().addItems(items.map { it.toString() }.toList())
        init()
    }

    override fun registerGraphic(): Node {
        return RadioGroup()
//        return TextField()
    }

    override fun graphic(): RadioGroup {
        return graphic as RadioGroup
    }

    override fun get(): R? {
        val text = graphic().text
        if (text.isBlank()) {
            return null
        }
        return graphic().text as R
    }

    override fun set(v: R?) {
        graphic().text = (v ?: defaultValue()).toString()
    }

    override fun clear() {
        graphic().text = defaultValue() as String
    }

    override fun verify(): Boolean {
        return false
    }

    override fun defaultValue(): R {
        return items[0]
    }
}