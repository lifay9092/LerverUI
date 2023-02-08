package cn.lifay.ui.form.radio

import cn.lifay.ui.form.FormElement
import javafx.scene.Node
import kotlin.reflect.KMutableProperty1


/**
 * RadioElement 选择组-元素
 * @author lifay
 * @date 2023/2/6 14:47
 **/
class RadioElement<T> constructor(
    label: String,
    property: KMutableProperty1<T, String?>,
    val items: List<String>
) : FormElement<T, String>(String::class.java, label, property) {

    //    private var items : Collection<String>
    init {
//        this.items = items
        graphic().addItems(items)
        init()
    }

    override fun registerGraphic(): Node {
        return RadioGroup()
//        return TextField()
    }

    override fun graphic(): RadioGroup {
        return graphic as RadioGroup
    }

    override fun get(): String? {
        val text = graphic().text
        if (text.isBlank()) {
            return null
        }
        return graphic().text
    }

    override fun set(v: String?) {
        graphic().text = v ?: defaultValue()
    }

    override fun clear() {
        graphic().text = defaultValue()
    }

    override fun verify(): Boolean {
        return false
    }

    override fun defaultValue(): String {
        return items[0]
    }
}