package cn.lifay.ui.form;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

/**
 * FormUI form表单：动态添加元素（文本框、选择），基本操作（保存、编辑、删除、清除）
 * @author lifay
 * @date 2023/1/8 10:05
 **/
public abstract class FormUI<T> extends Stage {

    private T t;
    private GridPane pane = new GridPane();
    ;
    protected ObservableList<FormElement<T, ?>> elements = FXCollections.observableArrayList();
    protected Button saveBtn = new Button("保存");
    protected Button editBtn = new Button("修改");
    protected Button delBtn = new Button("删除");
    protected Button clearBtn = new Button("清空");
    protected HBox btnGroup = new HBox(20);


    public FormUI() {
    }

    public FormUI(String title, Class<T> clazz) {
        try {
            t = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        formInit(title);
    }

    public FormUI(String title, T t) {
        this.t = t;
        formInit(title);
        propToElement();
    }

    private void formInit(String title) {
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(25, 25, 25, 25));
        this.setTitle(title);
        this.setScene(new Scene(pane));

        initElements();
    }

    /**
     * 加载布局
     *
     * @author lifay
     * @return
     */
    public void initElements() {
        /*网格布局：元素*/
        List<FormElement<T, ?>> elementList = buildElements();
        elements.addAll(elementList);
        //网格布局 算法 前提：纵向数量<=横向数量
        int h = 0;
        int v = 0;
        int size = elementList.size();
        //System.out.println("元素数量:" + size);
        double s = Math.sqrt(size);//开根号
        if ((int) s == s) {
            h = (int) s;
            v = (int) s;
        } else {
            h = (int) Math.ceil(s);
            v = (int) Math.round(s);
        }
        //System.out.println("横向数量="+h + " 竖向数量=" + v);
        //布局表单元素 0 0 ,1 0,01
        int elementIndex = 0;
        for (int x = 0; x < h; x++) {
            for (int y = 0; y < v; y++) {
                //System.out.println("x="+x + " y=" + y);
                FormElement<T, ?> element = elements.get(elementIndex);
                GridPane.setHalignment(element, HPos.LEFT);
                pane.add(element, y, x);
                elementIndex++;
                if (elementIndex == size) {
                    break;
                }
            }
        }
        pane.add(btnGroup, Math.round(h / 2), v);
        //布局按钮组
        btnGroup.setAlignment(Pos.BOTTOM_RIGHT);
        btnGroup.setSpacing(20);
        btnGroup.setPadding(new Insets(20));
        btnGroup.getChildren().addAll(saveBtn, editBtn, delBtn, clearBtn);
        //pane.getChildren().add(btnGroup);
        //保存
        saveBtn.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1) {
                try {
                    saveBtn.setDisable(true);
                    //从元素赋值到实例
                    elementToProp();
                    //执行保存操作
                    saveData(t);
                    Platform.runLater(() -> {
                        new Alert(Alert.AlertType.INFORMATION, "保存成功").show();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        new Alert(Alert.AlertType.ERROR, "保存失败:" + e.getMessage()).show();
                    });
                } finally {
                    saveBtn.setDisable(false);
                }
            }
        });
        //编辑
        editBtn.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1) {
                try {
                    editBtn.setDisable(true);
                    //从元素赋值到实例
                    elementToProp();
                    //执行保存操作
                    editData(t);
                    Platform.runLater(() -> {
                        new Alert(Alert.AlertType.INFORMATION, "编辑成功").show();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        new Alert(Alert.AlertType.ERROR, "编辑失败:" + e.getMessage()).show();
                    });
                } finally {
                    editBtn.setDisable(false);
                }
            }
        });
        //删除
        delBtn.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1) {
                try {
                    delBtn.setDisable(true);
                    //确认
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "是否删除?");
                    Optional<ButtonType> buttonType = alert.showAndWait();
                    if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
                        //从元素赋值到实例
                        elementToProp();
                        //获取主键值
                        Object idValue = getPrimaryValue();
                        if (idValue == null) {
                            throw new Exception("未获取到主键属性!");
                        }
                        //执行保存操作
                        delData(idValue);
                        clear();
                        Platform.runLater(() -> {
                            new Alert(Alert.AlertType.INFORMATION, "删除成功").show();
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        new Alert(Alert.AlertType.ERROR, "删除失败:" + e.getMessage()).show();
                    });
                } finally {
                    delBtn.setDisable(false);
                }
            }
        });
        //清空
        clearBtn.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1) {
                try {
                    clearBtn.setDisable(true);
                    clear();
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        new Alert(Alert.AlertType.ERROR, "操作失败:" + e.getMessage()).show();
                    });
                } finally {
                    clearBtn.setDisable(false);
                }
            }
        });
    }

    private void clear() {
        elements.forEach(FormElement::clear);
    }

    private Object getPrimaryValue() {
        for (FormElement<T, ?> element : elements) {
            if (element.getPrimary()) {
                return element.getElementValue();
            }
        }
        return null;
    }

    /**
     * 将组件数据赋值到实例属性
     *
     * @author lifay
     * @return void
     */
    private void elementToProp() throws Exception {
        try {
            for (FormElement<T, ?> element : elements) {
                /*Object value = element.getElementValue();
                Field field = t.getClass().getDeclaredField(element.getFieldName());
                field.setAccessible(true);
                field.set(t, value);*/
                //System.out.println("赋值了:" + element.getFieldName() + " > " + value);
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * 将实例属性赋值到组件数据
     *
     * @author lifay
     * @return void
     */
    private void propToElement() {
        try {
            for (FormElement<T, ?> element : elements) {
                /*Field field = t.getClass().getDeclaredField(element.getFieldName());
                field.setAccessible(true);
                Object value = field.get(t);
//                element.setValue(value);
                //主键不可编辑
                if (element.isPrimary()) {
                    element.disable();
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void checkId(Object id) {
        if (id == null) {
            throw new RuntimeException("主键值不能为空!");
        }
        boolean blank;
        if (id instanceof String) {
            blank = ((String) id).isBlank();
        } else if (id instanceof Integer) {
            blank = (Integer) id == 0;
        } else if (id instanceof Long) {
            blank = (Long) id == 0;
        } else {
            throw new RuntimeException("不支持当前类型:" + id.getClass());
        }
        if (blank) {
            throw new RuntimeException("主键值不能为空!");
        }
    }

    public abstract List<FormElement<T, ?>> buildElements();

    public abstract void saveData(T t);

    public abstract void editData(T t);

    public abstract void delData(Object primaryValue);

}
