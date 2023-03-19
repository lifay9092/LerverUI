package cn.lifay.ui.form.check

import cn.lifay.ui.DelegateProp
import cn.lifay.ui.form.FormElement
import javafx.scene.Node
import javafx.scene.control.CheckBox
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1

/**
 *@ClassName CheckElement
 *@Description 勾选框-元素
 *@Author lifay
 *@Date 2023/2/5 13:53
 **/
class CheckElement<T : Any, R : Any>(
    t: KClass<T>,
    r: Class<R>,
    label: String,
    property2: KMutableProperty1<T, R>?,
    property: KMutableProperty1<T, R?>?,
    customProp: DelegateProp<T, R>?,
    required: Boolean,
    initValue: R? = null
) :
    FormElement<T, R>(r, label, required = required, initValue = initValue) {

    init {
        super.property2 = property2
        super.property = property
        super.customProp = customProp
        super.tc = t

        init()
    }

    companion object {
        /*注入 property 返回值不为空 对应var 没有? */
        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            property: KMutableProperty1<T, R>,
            initValue: R? = null
        ) = CheckElement(T::class, R::class.java, label, property, null, null, true, initValue)

        /*注入 property 返回值不为空 对应var ? */
        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            property: KMutableProperty1<T, R?>,
            required: Boolean = false,
            initValue: R? = null
        ) = CheckElement(T::class, R::class.java, label, null, property, null, required, initValue)

        /*注入 customProp javabean */
        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            customProp: DelegateProp<T, R>,
            required: Boolean = false,
            initValue: R? = null
        ) = CheckElement(T::class, R::class.java, label, null, null, customProp, required, initValue)

    }

    override fun registerGraphic(): Node {
        return CheckBox()
    }

    override fun graphic(): CheckBox {
        return (node as CheckBox)
    }

    override fun getElementValue(): R {
        return convert(graphic().isSelected)
    }

    override fun setElementValue(v: R?) {

        graphic().isSelected = convert(v)
    }

    override fun clear() {
        graphic().isSelected = false
    }

    override fun verify(): Boolean {
        return false
    }

    override fun defaultValue(): R {
        return convert(graphic().isSelected)

    }

    /*
        fun getValue(): R? {
            return control.getValue()
        }

        fun setValue(r: Any) {
            control.setValue(r as R)
        }*/
    private fun convert(b: Boolean): R {
        return when (r) {
            java.lang.Boolean::class.java -> {
                b
            }

            java.lang.String::class.java -> {
                if (b) "1" else "0"
            }

            java.lang.Integer::class.java, java.lang.Long::class.java -> {
                if (b) 1 else 0
            }

            java.lang.Double::class.java, java.lang.Float::class.java -> {
                if (b) 1.0 else 0.0
            }

            else -> {
                println("not surport ${b} ${r}")
            }
        } as R
    }

    private fun convert(v: R?): Boolean {
        if (v == null) {
            return false
        }
        if (v is Boolean) {
            return v
        }
        return when (v) {
            "1", 1, 1.0 -> {
                true
            }

            else -> {
                false
            }
        }
    }
}