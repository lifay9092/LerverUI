package cn.lifay.test

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty


/**
 * TableTestVO TODO
 * @author lifay
 * @date 2023/2/20 17:44
 **/
class TableTestVO(
    var text: String,
    var info: String,
    var msg: SimpleStringProperty,
    var processBar: SimpleDoubleProperty
) {


}