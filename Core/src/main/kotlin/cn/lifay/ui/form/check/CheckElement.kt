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
    initValue: R? = null,
    val nodeBuild: (CheckBox.() -> Unit)?
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
            initValue: R? = null,
            noinline nodeBuild: (CheckBox.() -> Unit)? = null
        ) = CheckElement(T::class, R::class.java, label, property, null, null, true, initValue, nodeBuild)

        /*注入 property 返回值不为空 对应var ? */
        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            property: KMutableProperty1<T, R?>,
            required: Boolean = false,
            initValue: R? = null,
            noinline nodeBuild: (CheckBox.() -> Unit)? = null
        ) = CheckElement(T::class, R::class.java, label, null, property, null, required, initValue, nodeBuild)

        /*注入 customProp javabean */
        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            customProp: DelegateProp<T, R>,
            required: Boolean = false,
            initValue: R? = null,
            noinline nodeBuild: (CheckBox.() -> Unit)? = null
        ) = CheckElement(T::class, R::class.java, label, null, null, customProp, required, initValue, nodeBuild)

    }

    override fun registerGraphic(): Node {
        return CheckBox().apply {
            nodeBuild?.let { it(this) }
        }
    }

    override fun graphic(): CheckBox {
        return (node as CheckBox)
    }

    override fun getElementValue(): R {
        return convertBoolToR(graphic().isSelected)
    }

    override fun setElementValue(v: R?) {

        graphic().isSelected = convertToBool(v)
    }

    override fun clear() {
        graphic().isSelected = false
    }

    override fun verify(): Boolean {
        return true
    }

    override fun defaultValue(): R {
        return convertBoolToR(graphic().isSelected)

    }

    /*
        fun getValue(): R? {
            return control.getValue()
        }

        fun setValue(r: Any) {
            control.setValue(r as R)
        }*/


}