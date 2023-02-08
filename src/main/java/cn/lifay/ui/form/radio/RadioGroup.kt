package cn.lifay.ui.form.radio

import javafx.geometry.Pos
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.HBox

/**
 * RadioGroup 选择组
 * @author lifay
 * @date 2023/2/6 14:52
 **/
class RadioGroup() : HBox(10.0)  {
//    protected lateinit var items : Collection<String>
    protected val group = ToggleGroup()

    var text:String
        get() {
            return (group.toggles.first { it.isSelected } as RadioButton).text
        }
        set(value) {
            set(value)
        }

    init {
        this.alignment = Pos.CENTER
    }

    fun addItems(items : Collection<String>){
        for ((index,item) in items.withIndex()) {
            val button = RadioButton(item)
            button.toggleGroup = group
            if (index == 0) {
                button.isSelected = true
            }
            children.add(button)
        }
    }

    fun set(item :String){
        for (toggle in group.toggles) {
            val radioButton = toggle as RadioButton
            if (radioButton.text == item) {
                radioButton.isSelected = true
                break
            }
        }
    }

}