package cn.lifay.view

import cn.lifay.extension.copyToClipboard
import cn.lifay.ui.BaseView
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.layout.AnchorPane

class CommonView : BaseView<AnchorPane>() {

    /*
        rootPane 绑定了fxml中的顶级Pane的fx:id
     */
    @FXML
    var rootPane: AnchorPane = AnchorPane()

    /**
     * 注册根容器
     */
    override fun rootPane(): AnchorPane {
        return rootPane
    }

    @FXML
    private lateinit var baseViewCode: TextArea

    @FXML
    private lateinit var fxmlCode: TextArea

    @FXML
    private lateinit var otherCode: TextArea

    @FXML
    fun copyBaseViewCode(event: ActionEvent) {
        copyToClipboard(baseViewCode.text)
        showNotification("复制成功")
    }

    @FXML
    fun copyFxmlCode(event: ActionEvent) {
        copyToClipboard(fxmlCode.text)
        showNotification("复制成功")
    }

    @FXML
    fun copyOtherCode(event: ActionEvent) {
        copyToClipboard(otherCode.text)
        showNotification("复制成功")
    }
}
