package cn.lifay.ui.tree

import kotlin.reflect.KProperty1

/**
 *@ClassName TreeViewHelp
 *@Description TODO
 *@Author lifay
 *@Date 2023/3/5 18:17
 **/
class TreeViewHelp<T, R>() {

    lateinit var TREE_VIEW_PROP: Pair<KProperty1<T, R>, KProperty1<T, R>>

    val TREE_VIEW_INDEX = HashMap<R, String>()

    constructor(
        idProp: KProperty1<T, R>,
        parentProp: KProperty1<T, R>
    ) : this() {
        TREE_VIEW_PROP = Pair(idProp, parentProp)
    }

}