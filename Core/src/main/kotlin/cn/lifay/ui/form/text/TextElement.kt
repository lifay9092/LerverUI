package cn.lifay.ui.form.text

import cn.lifay.extension.borderColor
import cn.lifay.extension.platformRun
import cn.lifay.ui.DelegateProp
import cn.lifay.ui.form.FormElement
import javafx.scene.Node
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.TextInputControl
import javafx.scene.paint.Color
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1

/**
 * @ClassName TextElement
 * @Description 文本输入框元素
 * @Author 李方宇
 * @Date 2023/1/10 17:30
 */
class TextElement<T : Any, R : Any> constructor(
    t: KClass<T>,
    r: Class<R>,
    label: String,
    property2: KMutableProperty1<T, R>?,
    property: KMutableProperty1<T, R?>?,
    customProp: DelegateProp<T, R>?,
    primary: Boolean = false,
    required: Boolean = false,
    val isTextArea: Boolean = false,
    initValue: R? = null,
    val nodeBuild: (TextInputControl.() -> Unit)?
) :
    FormElement<T, R>(r, label, primary, required, initValue) {

    init {
        //   println("TextElement init")
        super.property2 = property2
        super.property = property
        super.customProp = customProp
        super.tc = t
        init()
    }

    companion object {
        /*注入 property 返回值不为空 对应var 没有? */
        @JvmName("TextElementNotNullKt")
        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            property: KMutableProperty1<T, R>,
            primary: Boolean = false,
            isTextArea: Boolean = false,
            initValue: R? = null,
            noinline nodeBuild: (TextInputControl.() -> Unit)? = null
        ) = TextElement(
            T::class,
            R::class.java,
            label,
            property,
            null,
            null,
            primary,
            true,
            isTextArea,
            initValue,
            nodeBuild
        )

        /*注入 property 返回值不为空 对应var ? */
        @JvmOverloads
        @JvmName("TextElementNullKt")
        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            property: KMutableProperty1<T, R?>,
            required: Boolean = false,
            primary: Boolean = false,
            isTextArea: Boolean = false,
            initValue: R? = null,
            noinline nodeBuild: (TextInputControl.() -> Unit)? = null
        ) = TextElement(
            T::class,
            R::class.java,
            label,
            null,
            property,
            null,
            primary,
            required,
            isTextArea,
            initValue,
            nodeBuild
        )

        /*注入 customProp javabean */
        @JvmOverloads
        @JvmName("TextElementNullJava")
        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            customProp: DelegateProp<T, R>,
            required: Boolean = false,
            primary: Boolean = false,
            isTextArea: Boolean = false,
            initValue: R? = null,
            noinline nodeBuild: (TextInputControl.() -> Unit)? = null
        ) = TextElement(
            T::class,
            R::class.java,
            label,
            null,
            null,
            customProp,
            primary,
            required,
            isTextArea,
            initValue,
            nodeBuild
        )

    }

    override fun registerGraphic(): Node {
        //  println("$label registerGraphic isTextArea:${_isTextArea}")
        val node = if (isTextArea) {
            TextArea().apply {
                isWrapText = true
                prefHeight = 170.0
                prefWidth = 250.0
                nodeBuild?.let { it(this) }
            }
        } else {
            val textField = TextField()
            nodeBuild?.let { it(textField) }
            textField
        }

        return node
    }

    override fun graphic(): TextInputControl {
        //  println("$label graphic isTextArea:${_isTextArea}")
        return node as TextInputControl
    }

    override fun getElementValue(): R? {
        val text = graphic().text
        if (text.isBlank()) {
            return null
        }
        return when (r) {
            java.lang.String::class.java -> {
                text.trim()
            }

            java.lang.Integer::class.java -> {
                text.trim().toInt()
            }

            java.lang.Long::class.java -> {
                text.trim().toLong()
            }

            java.lang.Double::class.java -> {
                text.trim().toDouble()
            }

            java.lang.Float::class.java -> {
                text.trim().toFloat()
            }

            else -> {
                println("not surport")
            }
        } as R?
    }

    override fun setElementValue(v: R?) {
        if (v == null) {
            graphic().text = defaultValue().toString()
        } else {
            graphic().text = v.toString()
        }
    }

    override fun clear() {
        if (node.isDisable) {
            return
        }
        platformRun {
            when (node) {
                is TextField -> {
                    (node as TextField).clear()
                }

                is TextArea -> {
                    (node as TextArea).clear()
                }
                else -> {
                }
            }
        }
    }

    override fun verify(): Boolean {
        if (graphic().text.isBlank()) {
//            graphic!!.borderColor("red")
            node!!.borderColor(Color.PINK)
            return false
        }
        node!!.style = null
        return true
    }

    override fun defaultValue(): R {
        return when (r) {
            java.lang.String::class.java -> {
                ""
            }

            java.lang.Integer::class.java, java.lang.Long::class.java -> {
                0
            }

            java.lang.Double::class.java, java.lang.Float::class.java -> {
                0.0
            }

            else -> {}
        } as R
    }
}