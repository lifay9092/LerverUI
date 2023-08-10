package cn.lifay.test

import javafx.beans.property.SimpleStringProperty


/**
 * TreeTestVO TODO
 * @author lifay
 * @date 2023/2/20 17:44
 **/
class TreeTestVO(
    var id: String,
    var parentId: String,
    var name: String,
    var nameProp: SimpleStringProperty
) {
    override fun toString(): String {
        return name
    }
}