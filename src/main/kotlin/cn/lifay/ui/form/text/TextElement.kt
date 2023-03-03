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
import kotlin.reflect.KMutableProperty1

/**
 * @ClassName TextElement
 * @Description 文本输入框元素
 * @Author 李方宇
 * @Date 2023/1/10 17:30
 */
class TextElement<T : Any, R : Any> constructor(
    r: Class<R>,
    label: String,
    property: KMutableProperty1<T, R?>?,
    customProp: DelegateProp<T, R>? = null,
    primary: Boolean = false,
    required: Boolean = false,
    private val isTextArea: Boolean = false
) :
    FormElement<T, R>(r, label, property, customProp, primary, required) {

    /*constructor(r: Class<R>,
                label: String,
                property: KMutableProperty1<T, R>?,
                customProp: DelegateProp<T, R>? = null,
                primary: Boolean = false,
                required: Boolean = false,
                isTextArea: Boolean = false
    ):this(r,label, property, customProp, primary, required)*/
    companion object {
        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            property: KMutableProperty1<T, R?>,
            primary: Boolean = false,
            required: Boolean = false,
            isTextArea: Boolean = false
        ) = TextElement(R::class.java, label, property, null, primary, required, isTextArea){

        }
        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            property: KMutableProperty1<T, R>,
            primary: Boolean = false,
            required: Boolean = false,
            isTextArea: Boolean = false
        ) = TextElement(R::class.java, label, property, null, primary, required, isTextArea)

        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            customProp: DelegateProp<T, R>,
            primary: Boolean = false,
            required: Boolean = false,
            isTextArea: Boolean = false
        ) = TextElement(R::class.java, label, null, customProp, primary, required, isTextArea)

    }

    constructor(
        r: Class<R>,
        label: String,
        property: KMutableProperty1<T, R>,
        customProp: Nothing?,
        primary: Boolean,
        required: Boolean,
        isTextArea: Boolean
    ) : this(r,label, property, customProp, primary, required)

    init {
        init()
    }

    override fun registerGraphic(): Node {
        return if (isTextArea) {
            val textArea = TextArea()
            textArea.isWrapText = false
            textArea
        } else {
            val textField = TextField()
            textField
        }
    }

    override fun graphic(): TextInputControl {
        return graphic as TextInputControl
    }

    override fun get(): R? {
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

    override fun set(v: R?) {
        if (v == null) {
            graphic().text = defaultValue?.toString() ?: defaultValue as String
        } else {
            graphic().text = v.toString()
        }
    }

    override fun clear() {
        platformRun {
            when (graphic) {
                is TextField -> {
                    graphic.clear()
                    if (primary) {
                        graphic.setDisable(false)
                    }
                }

                is TextArea -> {
                    graphic.clear()
                    if (primary) {
                        graphic.setDisable(false)
                    }
                }

                else -> {
                }
            }
        }
    }

    override fun verify(): Boolean {
        if (graphic().text.isBlank()) {
//            graphic!!.borderColor("red")
            graphic!!.borderColor(Color.PINK)
            return false
        }
        graphic!!.style = null
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