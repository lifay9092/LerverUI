package cn.lifay.ui.tree

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.Event
import javafx.event.EventType
import javafx.scene.Node
import javafx.scene.control.TreeItem

class CustomCheckBoxTreeItem<T : Any> @JvmOverloads constructor(
    var1: T? = null,
    var2: Node? = null,
    var3: Boolean = false,
    var4: Boolean = false
) : CustomTreeItem<T>(var1, var2) {


    private var CHECK_BOX_SELECTION_CHANGED_EVENT: EventType<out Event>? = null
    private var stateChangeListener: ChangeListener<Boolean?>? = null
    private var selected: BooleanProperty = SimpleBooleanProperty(false)
    private var indeterminate: BooleanProperty? = null
    private var independent: BooleanProperty? = null
    private var updateLock = false

    companion object {
        private var CHECK_BOX_SELECTION_CHANGED_EVENT: EventType<out Event?>? = null
        private var updateLock = false
        fun <T> checkBoxSelectionChangedEvent(): EventType<out Event?>? {
            return CHECK_BOX_SELECTION_CHANGED_EVENT
        }

        init {
            CHECK_BOX_SELECTION_CHANGED_EVENT = EventType(
                TreeModificationEvent.ANY,
                "checkBoxSelectionChangedEvent"
            )
            updateLock = false
        }
    }

    init {
        stateChangeListener =
            ChangeListener { var1x: ObservableValue<out Boolean?>?, var2x: Boolean?, var3x: Boolean? -> updateState() }
        selected = object : SimpleBooleanProperty(this, "selected", false) {
            override fun invalidated() {
                super.invalidated()
                this@CustomCheckBoxTreeItem.fireEvent(this@CustomCheckBoxTreeItem, true)
            }
        }
        indeterminate = object : SimpleBooleanProperty(this, "indeterminate", false) {
            override fun invalidated() {
                super.invalidated()
                this@CustomCheckBoxTreeItem.fireEvent(this@CustomCheckBoxTreeItem, false)
            }
        }
        independent = SimpleBooleanProperty(this, "independent", false)
        setSelected(var3)
        setIndependent(var4)
        selectedProperty()?.addListener(stateChangeListener)
        indeterminateProperty()?.addListener(stateChangeListener)
    }

    class TreeModificationEvent<T : Any>(
        var1: EventType<out Event?>?,
        @field:Transient val treeItem: CustomCheckBoxTreeItem<T>,
        private val selectionChanged: Boolean
    ) : Event(var1) {
        fun wasSelectionChanged(): Boolean {
            return selectionChanged
        }

        fun wasIndeterminateChanged(): Boolean {
            return !selectionChanged
        }

        companion object {
            private const val serialVersionUID = -3909698965886000594L
            val ANY = EventType(Event.ANY, "CUSTOM_TREE_MODIFICATION")

        }
    }

    fun <T> checkBoxSelectionChangedEvent(): EventType<out Event>? {
        return CHECK_BOX_SELECTION_CHANGED_EVENT
    }


    fun setSelected(var1: Boolean) {
        selectedProperty()!!.value = var1
    }

    fun isSelected(): Boolean {
        return selected!!.value
    }

    fun selectedProperty(): BooleanProperty {
        return selected
    }

    fun setIndeterminate(var1: Boolean) {
        indeterminateProperty()!!.value = var1
    }

    fun isIndeterminate(): Boolean {
        return indeterminate!!.value
    }

    fun indeterminateProperty(): BooleanProperty? {
        return indeterminate
    }

    fun independentProperty(): BooleanProperty? {
        return independent
    }

    fun setIndependent(var1: Boolean) {
        independentProperty()!!.value = var1
    }

    fun isIndependent(): Boolean {
        return independent!!.value
    }

    private fun updateState() {
        if (!isIndependent()) {
            val var1 = !updateLock
            updateLock = true
            updateUpwards()
            if (var1) {
                updateLock = false
            }
            if (!updateLock) {
                updateDownwards()
            }
        }
    }

    private fun updateUpwards() {
        if (parent is CustomCheckBoxTreeItem<*>) {
            val var1 = parent as CustomCheckBoxTreeItem<*>
            var var2 = 0
            var var3 = 0
            val var4: Iterator<*> = var1.children.iterator()
            while (true) {
                var var5: TreeItem<*>?
                do {
                    if (!var4.hasNext()) {
                        if (var2 == var1.children.size) {
                            var1.setSelected(true)
                            var1.setIndeterminate(false)
                        } else if (var2 == 0 && var3 == 0) {
                            var1.setSelected(false)
                            var1.setIndeterminate(false)
                        } else {
                            var1.setIndeterminate(true)
                        }
                        return
                    }
                    var5 = var4.next() as TreeItem<*>?
                } while (var5 !is CustomCheckBoxTreeItem<*>)
                val var6 = var5
                var2 += if (var6.isSelected() && !var6.isIndeterminate()) 1 else 0
                var3 += if (var6.isIndeterminate()) 1 else 0
            }
        }
    }

    private fun updateDownwards() {
        if (!this.isLeaf) {
            val var1: Iterator<*> = children.iterator()
            while (var1.hasNext()) {
                val var2 = var1.next() as TreeItem<*>
                if (var2 is CustomCheckBoxTreeItem<*>) {
                    var2.setSelected(isSelected())
                }
            }
        }
    }

    private fun fireEvent(var1: CustomCheckBoxTreeItem<T>, var2: Boolean) {
        val var3 = TreeModificationEvent(CHECK_BOX_SELECTION_CHANGED_EVENT, var1, var2)
        Event.fireEvent(this, var3)
    }


}