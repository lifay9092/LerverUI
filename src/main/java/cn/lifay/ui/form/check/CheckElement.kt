package cn.lifay.ui.form.check

import cn.lifay.ui.form.FormElement
import javafx.scene.Node
import javafx.scene.control.CheckBox
import kotlin.reflect.KMutableProperty1

/**
 *@ClassName CheckElement
 *@Description 勾选框-元素
 *@Author lifay
 *@Date 2023/2/5 13:53
 **/
class CheckElement<T, R : Any>(
    r: Class<R>,
    label: String,
    property: KMutableProperty1<T, R?>
) :
    FormElement<T, R>(r, label, property) {

    companion object {
        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            property: KMutableProperty1<T, R?>
        ) = CheckElement(R::class.java, label, property)
    }

    init {
        init()
    }

    override fun registerGraphic(): Node {
        return CheckBox()
    }

    override fun graphic(): CheckBox {
        return (graphic as CheckBox)
    }

    override fun get(): R {
        return convert(graphic().isSelected)
    }

    override fun set(v: R?) {

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