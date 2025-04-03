package cn.lifay.temp

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView

class BindableTreeItem<T>(initialValue: T) : TreeItem<T>() {
    private val valueProperty = SimpleObjectProperty<T>(initialValue)

    init {
        valueProperty.addListener { _, _, newValue ->
            this.value = newValue
        }
    }

    fun bind(bindable: ObservableValue<T>) {
        valueProperty.bind(bindable)
    }
}

// 扩展函数
fun <T> TreeView<T>.bindItems(
    dataSource: ObservableList<BindableTreeItem<T>>,
    converter: (T) -> String = { it.toString() }
) {
    root = TreeItem<T>().apply {
        children.setAll(dataSource)
    }

    setCellFactory {
        object : TreeCell<T>() {
            override fun updateItem(item: T, empty: Boolean) {
                super.updateItem(item, empty)
                text = if (empty || item == null) "" else converter(item)
            }
        }
    }
}