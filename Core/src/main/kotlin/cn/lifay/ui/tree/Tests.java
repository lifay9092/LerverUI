package cn.lifay.ui.tree;

import javafx.scene.control.TreeView;

public class Tests {

    void dd() {

        TreeView<String> treeView = new TreeView<>();
        treeView.rootProperty().addListener((observableValue, stringTreeItem, t1) -> {

        });
    }
}
