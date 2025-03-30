package cn.lifay.test

import cn.lifay.application.BaseApplication
import cn.lifay.ui.BaseView
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage
import org.graphstream.graph.Graph
import org.graphstream.graph.implementations.SingleGraph
import org.graphstream.ui.fx_viewer.FxViewPanel
import org.graphstream.ui.fx_viewer.FxViewer
import org.graphstream.ui.view.Viewer


/*
 * Common 测试类
 *
 * @author lifay
 * @date 2023/1/9 16:07
 */
class CommonApplication : BaseApplication() {

    override fun start(primaryStage: Stage) {
//        start1(primaryStage)
//        //AnchorPane跟CommonView中的Pane类型一致
        val view = BaseView.createView<CommonDemoView, AnchorPane>(
//            CommonApplication::class.java.getResource("common.fxml")
            CommonApplication::class.java.getResource("demo.fxml")
        )
        val scene = Scene(view.ROOT_PANE)
        primaryStage.title = "Hello World"
        primaryStage.scene = scene
        primaryStage.setOnCloseRequest {
            println("close...")
        }
        primaryStage.show()
    }

    fun start1(stage: Stage) {
        // 创建图对象
        val graph: Graph = SingleGraph("功能结构图")
        graph.setAttribute("ui.stylesheet", getStyleSheet()) // 加载 CSS 样式

        // 定义节点及其父子关系（根据附件内容手动构建）
        // 第一层根节点
        val rootId = "点云数据与台账关联"
        graph.addNode(rootId)

        // 第二层节点（直接子节点）
        val secondLevelNodes = arrayOf(
            "组织结构台账数据",
            "输电线路台账数据",
            "三维自建线路信息维护",
            "杆塔台账数据接入"
        )
        for (node in secondLevelNodes) {
            graph.addNode(node).setAttribute("ui.label", "${node}\n(${rootId})");
            graph.addEdge("$rootId-$node", rootId, node)
        }

        // 第三层节点（示例：以 "杆塔台账数据接入" 的子节点为例）
        val thirdLevelNodes = arrayOf(
            "三维杆塔数据连接信息",
            "三维杆塔信息",
            "三维自建杆塔信息",
            "KML匹配"
        )
        for (node in thirdLevelNodes) {
            graph.addNode(node).setAttribute("ui.label", "${node}\n(${rootId})");
            graph.addEdge("$rootId-$node", rootId, node)
        }

        // 更多层级按需添加...

        // 应用自动布局算法
//        val layout = SpringBox()
//        layout.quality = 1.0 // 控制布局紧凑度
//        layout.grap
//        layout.compute()

        // 创建 JavaFX 视图
        val viewer = FxViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD)
        viewer.enableAutoLayout()
        //      viewer.addDefaultView(true) // 显示默认视图
//         val panel = viewer.addDefaultView(false, FxGraphRenderer()) as FxViewPanel
        val panel = viewer.addDefaultView(false) as FxViewPanel
        // 显示窗口
        stage.scene = Scene(panel, 1200.0, 800.0)
        stage.title = "功能结构图"
        stage.show()

        // 导出为 PNG（需要手动触发截图）
        // WritableImage image = viewer.getDefaultView().snapshot(null, null);
        // File file = new File("structure.png");
        // ImageIO.write(SwingFXUtils.fromFXImage(image, null), "PNG", file);
    }

    // 定义 CSS 样式（区分层级）
    private fun getStyleSheet(): String {
        return """
            node {
                shape: box;                
                size-mode: fit;            
                fill-color: #FFFFFF;      
                stroke-mode: plain;       
                stroke-color: #666666;     
                stroke-width: 1px;       
                padding: 10px;           
                text-alignment: center;   
                text-size: 14px;         
                text-color: #333333;       
                text-style: bold;          
            }
            node:clicked {
                fill-color: #E0E0E0;      
            }
        """;
    }
}