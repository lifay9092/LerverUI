package cn.lifay.ui.table

import javafx.event.Event
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent


/**
 * TableEditCell 自定义可编辑表格单元
 *  ```
 * 	 TableColumn<TableTestVO, String>("普通").apply {
 *       this.cellValueFactory = PropertyValueFactory("text")
 *       setCellFactory {
 *           object : TableEditCell<TableTestVO, String>() {}
 *       }
 *       setOnEditCommit { t: TableColumn.CellEditEvent<TableTestVO, String> ->
 *           (t.tableView.items[t.tablePosition.row] as TableTestVO).text = t.newValue
 *       }
 *        prefWidth = 150.0
 *   }
 * 	```
 * @author lifay
 * @date 2023/3/6 14:28
 **/
open class TableEditCell<T, R> : TableCell<T, R>() {


    private var textField: TextField? = null

    override fun startEdit() {
        if (!isEmpty) {
            super.startEdit()
            createTextField()
            text = null
            graphic = textField
            textField!!.selectAll()
        }
    }

    override fun cancelEdit() {
        super.cancelEdit()
        text = item as String
        graphic = null
    }

    override fun updateItem(item: R?, empty: Boolean) {
        super.updateItem(item, empty)
        if (empty) {
            text = null
            setGraphic(null)
        } else {
            if (isEditing) {
                if (textField != null) {
                    textField!!.text = getString()
                }
                text = null
                setGraphic(textField)
            } else {
                text = getString()
                setGraphic(null)
            }
        }
    }

    override fun commitEdit(newValue: R) {
        if (!isEditing && newValue != item) {
            val table: TableView<T> = tableView
            if (tableView != null) {
                val col: TableColumn<T, R> = tableColumn
                val event: TableColumn.CellEditEvent<T, R> = TableColumn.CellEditEvent<T, R>(
                    table,
                    TablePosition(table, index, col), TableColumn.editCommitEvent(),
                    newValue
                )
                Event.fireEvent(col, event)
            }
        }
        super.commitEdit(newValue)
        updateItem(newValue, false)
    }

    private fun createTextField() {
        textField = TextField(getString())
        textField!!.setMinWidth(width - graphicTextGap * 2)
        textField!!.focusedProperty().addListener { ob, old, now ->
            if (!now) {
                commitEdit(textField!!.text as R)
            }
        }
        textField!!.setOnKeyReleased { var1x: KeyEvent ->
            if (var1x.code == KeyCode.ENTER) {
             //   println("enter")
                commitEdit(textField!!.text as R)
            }
            if (var1x.code == KeyCode.ESCAPE) {
            //    println("esc")
                cancelEdit()
                var1x.consume()
            }
        }
    }

    private fun getString(): String? {
        return if (item == null) "" else item.toString()
    }

}