package cn.lifay.ui.tree

import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.util.Callback
import javafx.util.StringConverter

class LerverCheckBoxTreeCell<T : Any> private constructor(
    getSelectedProperty: Callback<TreeItem<T?>?, ObservableValue<Boolean>?>,
    converter: StringConverter<TreeItem<T?>?>,
    getIndeterminateProperty: Callback<TreeItem<T>, ObservableValue<Boolean>>?,
    val customFuncNodes: ((TreeItem<T>?) -> List<Node>)? = null
) : TreeCell<T>() {

    private val checkBox = CheckBox().apply {
        alignment = Pos.CENTER
    }

    //    private var hBox : HBox? = HBox(3.0).apply {
//        alignment = Pos.CENTER_LEFT
//    }
    private var hBox: HBox? = null
    private var booleanProperty: ObservableValue<Boolean>? = null
    private var indeterminateProperty: BooleanProperty? = null

    @JvmOverloads
    constructor(
        getSelectedProperty: Callback<TreeItem<T?>?, ObservableValue<Boolean>?> = Callback<TreeItem<T?>?, ObservableValue<Boolean>?> { item: TreeItem<T?>? ->
            if (item is CheckBoxTreeItem<*>) {
                return@Callback (item as CheckBoxTreeItem<*>).selectedProperty()
            }
            null
        }
    ) : this(getSelectedProperty, object : StringConverter<TreeItem<T?>?>() {
        override fun toString(treeItem: TreeItem<T?>?): String {
            return if (treeItem?.value == null) "" else treeItem.value.toString()
        }

        override fun fromString(string: String?): TreeItem<T?>? {
            return TreeItem(string as T?)
        }
    }, null)

    constructor(
        getSelectedProperty: Callback<TreeItem<T?>?, ObservableValue<Boolean>?>,
        converter: StringConverter<TreeItem<T?>?>
    ) : this(getSelectedProperty, converter, null)

    constructor(
        customFuncNodes: (TreeItem<T>?) -> List<Node>
    ) : this(Callback<TreeItem<T?>?, ObservableValue<Boolean>?> { item: TreeItem<T?>? ->
        if (item is CheckBoxTreeItem<*>) {
            return@Callback (item as CheckBoxTreeItem<*>).selectedProperty()
        }
        null
    }, object : StringConverter<TreeItem<T?>?>() {
        override fun toString(treeItem: TreeItem<T?>?): String {
            return if (treeItem?.value == null) "" else treeItem.value.toString()
        }

        override fun fromString(string: String?): TreeItem<T?>? {
            return TreeItem(string as T?)
        }
    }, null, customFuncNodes)

    /* *************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/
    // --- converter
    private val converter: ObjectProperty<StringConverter<TreeItem<T?>?>> = SimpleObjectProperty(this, "converter")

    /**
     * The [StringConverter] property.
     * @return the [StringConverter] property
     */
    fun converterProperty(): ObjectProperty<StringConverter<TreeItem<T?>?>> {
        return converter
    }

    /**
     * Sets the [StringConverter] to be used in this cell.
     * @param value the [StringConverter] to be used in this cell
     */
    fun setConverter(value: StringConverter<TreeItem<T?>?>) {
        converterProperty().set(value)
    }

    /**
     * Returns the [StringConverter] used in this cell.
     * @return the [StringConverter] used in this cell
     */
    fun getConverter(): StringConverter<TreeItem<T?>?> {
        return converterProperty().get()
    }

    // --- selected state callback property
    private val selectedStateCallback: ObjectProperty<Callback<TreeItem<T?>?, ObservableValue<Boolean>?>> =
        SimpleObjectProperty(
            this, "selectedStateCallback"
        )

    init {
        this.styleClass.add("check-box-tree-cell")
        setSelectedStateCallback(getSelectedProperty)
        setConverter(converter)
        checkBox.isAllowIndeterminate = false

        // by default the graphic is null until the cell stops being empty
        graphic = null
    }

    /**
     * Property representing the [Callback] that is bound to by the
     * CheckBox shown on screen.
     * @return the property representing the [Callback] that is bound to
     * by the CheckBox shown on screen
     */
    fun selectedStateCallbackProperty(): ObjectProperty<Callback<TreeItem<T?>?, ObservableValue<Boolean>?>> {
        return selectedStateCallback
    }

    /**
     * Sets the [Callback] that is bound to by the CheckBox shown on screen.
     * @param value the [Callback] that is bound to by the CheckBox shown on screen
     */
    fun setSelectedStateCallback(value: Callback<TreeItem<T?>?, ObservableValue<Boolean>?>) {
        selectedStateCallbackProperty().set(value)
    }

    /**
     * Returns the [Callback] that is bound to by the CheckBox shown on screen.
     * @return the [Callback] that is bound to by the CheckBox shown on screen
     */
    fun getSelectedStateCallback(): Callback<TreeItem<T?>?, ObservableValue<Boolean>?> {
        return selectedStateCallbackProperty().get()
    }

    /* *************************************************************************
     *                                                                         *
     * Static cell factories                                                   *
     *                                                                         *
     **************************************************************************/
    companion object {

        fun <T : Any> forTreeView(): Callback<TreeView<T>, TreeCell<T>> {
            val getSelectedProperty =
                Callback<TreeItem<T?>?, ObservableValue<Boolean>?> { item: TreeItem<T?>? ->
                    if (item is CheckBoxTreeItem<T?>) {
                        return@Callback (item as CheckBoxTreeItem<T>).selectedProperty()
                    }
                    null
                }
            val lerverCheckBoxTreeCell = LerverCheckBoxTreeCell(
                getSelectedProperty,
                object : StringConverter<TreeItem<T?>?>() {
                    override fun toString(treeItem: TreeItem<T?>?): String {
                        return if (treeItem?.value == null) "" else treeItem.value.toString()
                    }

                    override fun fromString(string: String?): TreeItem<T?>? {
                        return TreeItem(string as T?)
                    }
                }, null, null
            )
            return Callback { tree: TreeView<T>? ->
                lerverCheckBoxTreeCell
            }
        }


    }

    private var isAdd: Boolean = false
    /* *************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/
    /** {@inheritDoc}  */
    override fun updateItem(item: T?, empty: Boolean) {
        super.updateItem(item, empty)
//        println("updateItem...")
        if (item == null || empty) {
//            hBox = null
            text = null
            setGraphic(null)
        } else {
            if (hBox != null && graphic != null) return
            if (hBox == null) {
                //  println("hbox....")
                hBox = HBox(3.0).apply {
                    alignment = Pos.TOP_LEFT
                }
            }
            val c = getConverter()
            val customNodes = if (customFuncNodes == null) emptyList() else customFuncNodes!!.invoke(treeItem)
            if (customNodes.isNotEmpty()) {
                text = null
                hBox!!.children.setAll(checkBox, *customNodes.toTypedArray())
            } else {
                if (item is Node) {
                    text = null

                    hBox!!.children.setAll(checkBox, item as Node?)
                } else {
                    val s = c.toString(treeItem)
                    text = s
                    hBox!!.children.setAll(checkBox)
                }
            }
            graphic = hBox

            // uninstall bindings
            if (booleanProperty != null) {
                checkBox.selectedProperty().unbindBidirectional(booleanProperty as BooleanProperty?)
            }
            if (indeterminateProperty != null) {
                checkBox.indeterminateProperty().unbindBidirectional(indeterminateProperty)
            }

            // install new bindings.
            // We special case things when the TreeItem is a CheckBoxTreeItem
            if (treeItem is CheckBoxTreeItem<*>) {
                val cbti = treeItem as CheckBoxTreeItem<T?>
                booleanProperty = cbti.selectedProperty()
                checkBox.selectedProperty().bindBidirectional(booleanProperty as BooleanProperty?)
                indeterminateProperty = cbti.indeterminateProperty()
                checkBox.indeterminateProperty().bindBidirectional(indeterminateProperty)
            } else {
                val callback = getSelectedStateCallback()
                    ?: throw NullPointerException(
                        "The CheckBoxTreeCell selectedStateCallbackProperty can not be null"
                    )
                booleanProperty = callback.call(treeItem)
                if (booleanProperty != null) {
                    checkBox.selectedProperty().bindBidirectional(booleanProperty as BooleanProperty?)
                }
            }
        }
    }

}
