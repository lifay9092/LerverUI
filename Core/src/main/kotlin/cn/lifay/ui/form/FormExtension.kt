package cn.lifay.ui.form

import cn.lifay.extension.styleWarn
import cn.lifay.ui.form.btn.BaseButton
import javafx.scene.control.Button


fun <T : Any> BaseFormUI<T>.clearBtn(name: String = "重置"): BaseButton<BaseFormUI<T>> {
    return BaseButton(Button(name).styleWarn()) { baseFormUI ->
        baseFormUI.elements().forEach { it.clear() }
    }
}