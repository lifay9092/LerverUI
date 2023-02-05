package cn.lifay.ui.form.text

import cn.lifay.ui.form.FormElement
import javafx.scene.control.Control
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import kotlin.reflect.KMutableProperty1

/**
 * @ClassName TextElement
 * @Description TODO
 * @Author 李方宇
 * @Date 2023/1/10 17:30
 */
class TextNewElement<T, R : Any> constructor(
    label: String,
    property: KMutableProperty1<T, R>,
    primary: Boolean = false,
    var isTextArea: Boolean = false
) :
    FormElement<T, R>(label, property, primary) {
    private var textField: TextField? = null
    private var textArea: TextArea? = null

    init {
        init()
    }

    override fun control(): Control {
        return if (isTextArea) {
            if (textArea == null) {
                textArea = TextArea()
                textArea!!.isWrapText = false
            }
            textArea!!
        } else {
            if (textField == null) {
                textField = TextField()
            }
            textField!!
        }
    }

}