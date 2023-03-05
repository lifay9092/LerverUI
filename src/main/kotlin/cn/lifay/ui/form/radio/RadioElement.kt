package cn.lifay.ui.form.radio

import cn.lifay.ui.DelegateProp
import cn.lifay.ui.form.FormElement
import javafx.scene.Node
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1


/**
 * RadioElement 选择组-元素
 * @author lifay
 * @date 2023/2/6 14:47
 **/
class RadioElement<T : Any, R : Any>(
    t: KClass<T>,
    r: Class<R>,
    label: String,
    property2: KMutableProperty1<T, R>?,
    property: KMutableProperty1<T, R?>?,
    customProp: DelegateProp<T, R>?,
    private var items: List<R>,
    required: Boolean
) : FormElement<T, R>(r, label, required = required) {

    init {
        super.property2 = property2
        super.property = property
        super.customProp = customProp
        super.tc = t
        graphic().addItems(items.map { it.toString() }.toList())
        init()
    }

    companion object {
        /*注入 property 返回值不为空 对应var 没有? */
        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            property: KMutableProperty1<T, R>,
            items: List<R>
        ) = RadioElement(T::class, R::class.java, label, property, null, null, items, true)

        /*注入 property 返回值不为空 对应var ? */
        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            property: KMutableProperty1<T, R?>,
            items: List<R>,
            required: Boolean = false
        ) = RadioElement(T::class, R::class.java, label, null, property, null, items, required)

        /*注入 customProp javabean */
        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            customProp: DelegateProp<T, R>,
            items: List<R>,
            required: Boolean = false
        ) = RadioElement(T::class, R::class.java, label, null, null, customProp, items, required)
    }


    override fun registerGraphic(): Node {
        return RadioGroup()
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