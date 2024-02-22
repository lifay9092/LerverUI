package cn.lifay.ui.tree

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import java.util.function.Function
import kotlin.reflect.KProperty1

class TestTreeView<T : Any, P : Any> : TreeView<T> {
    constructor()
    constructor(treeItem: TreeItem<T>?) : super(treeItem)


    fun sss(
        panTreeItem: TreeItem<T>,
        idProp: KProperty1<T, P>,
        lerverTreeTempNodeChildren: Collection<LerverTreeTempNode<T>>?,
        filterFunc: Function<T, P>? = null
    ) {
        when (LerverTreeDataType.TREE) {
            LerverTreeDataType.LIST -> {

            }
            else -> {

            }
        }




    }

}