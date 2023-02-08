package cn.lifay.ui.form.text

import cn.lifay.ui.form.FormElement
import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.*
import java.awt.SystemColor.text
import kotlin.reflect.KMutableProperty1

/**
 * @ClassName TextElement
 * @Description 文本输入框元素
 * @Author 李方宇
 * @Date 2023/1/10 17:30
 */
class TextNewElement<T, R : Any> constructor(
    r: Class<R>,
    label: String,
    property: KMutableProperty1<T, R?>,
    primary: Boolean = false,
    required: Boolean = false,
    private var isTextArea: Boolean = false
) :
    FormElement<T, R>(r, label, property, primary,required) {

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

    override fun clear(){
        Platform.runLater {
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
            graphic!!.style = "-fx-border-color: red;"
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
            java.lang.Integer::class.java,java.lang.Long::class.java -> {
                0
            }
            java.lang.Double::class.java,java.lang.Float::class.java -> {
                0.0
            }
            else -> {}
        } as R
    }
}