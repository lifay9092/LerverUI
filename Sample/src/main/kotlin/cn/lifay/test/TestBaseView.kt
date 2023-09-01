package cn.lifay.test

import cn.lifay.ui.BaseView
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane


/**
 * TestBaseView TODO
 * @author lifay
 * @date 2023/9/1 10:52
 **/
class TestBaseView : BaseView<AnchorPane>(TestBaseView::class.java.getResource("baseView.fxml")) {

    @FXML
    private val rootPane = AnchorPane()

    @FXML
    private lateinit var notificationText : TextField
    /**
     * 注册根容器
     */
    override fun rootPane(): AnchorPane {
        return rootPane
    }
}