# LerverUI简介

LerverUI是一个基于javaFX框架封装了部分UI组件、常用操作的框架,语言采用 [Kotlin](https://book.kotlincn.net/text/getting-started.html)。

---

部分功能(模态窗口提示、弹出通知、样式)引用和借鉴了[easyfx](https://github.com/xizi110/easyfx)。

JavaFX的样式UI组件引用了[atlantafx](https://github.com/mkpaz/atlantafx)。

---

### 使用方式，通过Maven引入

```
<dependency>
    <groupId>cn.lifay.LerverUI</groupId>
    <artifactId>Core</artifactId>
    <version>1.10-SNAPSHOT</version>
</dependency>

<repositories>
    <repository>
        <id>lifay-public</id>
        <url>http://www.lifay.cn:8081/repository/maven-public/</url>
    </repository>
</repositories>
```

**一.常用操作**

1.初始化样式主题

```
//默认PrimerLight
GlobalResource.loadTheme()

//指定PrimerDark
GlobalResource.loadTheme(PrimerDark())
```

2.为窗体指定图标（图标默认路径：/icon.png，即将icon.png放在resources目录下）

```
//为stage指定图标（如果是框架创建的stage，一般已经默认设置了）
GlobalResource.loadIcon(stage)

//自定义图标路径
GlobalResource.setGlobalIconImage(imgPath)
```

3.主线程执行(Platform.runLater)

```
platformRun{
    tudo()
}

```

4.校验参数

```
if(!checkParam("名称",name)){
    return
}
```

5.异步协程执行

```
asyncTask {
    //后台执行
}
```

6.异步延迟执行耗时操作 时间：毫秒

```
asyncDelayTask(500) {
    platformRun { closeFunc() }
}
```

7.异步执行耗时操作,同时有加载图标提示

```
asyncTaskLoading(ROOT_PANE.scene.window, "保存中") {
    try {
        //后台操作
    } catch (e: Exception) {
        e.printStackTrace()
        showErrMessage("保存失败:" + e.message)
    } finally {
        //结束操作
    }
}
```

8.为Stage绑定快捷键:ESC关闭窗口

```
stage.bindEscKey()
```

9.常用弹出提示

```
alertInfo("信息打印")
alertWarn("警告打印")
alertError(
    "错误打印","头部信息", "异常详细信息fun tableText(actionEvent: ActionEvent) {\n" +
            "        tableView.items[0].text = \"33333\"\n" +
    
            "    }\n"
)
```

10.快速复制文本到粘贴板

```
copyToClipboard("文字")
```

**二.消息总线**

> 消息总线一般是为了解决跨界面无耦合回调执行操作，并且可以多界面同时触发

```
使用方法：

1.为事件定义枚举类ID，实现EventBusId

enum class DemoId : EventBusId {
    RELOAD_UI,
    CHAT,
}

2.订阅注册：将来会被触发执行的匿名函数

//DefaultEvent为内置无参事件传输DTO
EventBus.subscribe(DemoId.RELOAD_UI, DefaultEvent::class) {
    platformRun {
        tableView.refresh()
    }
}
//TextEvent为内置事件传输DTO，包含一个String类型参数
EventBus.subscribe(DemoId.CHAT, TextEvent::class) {
    platformRun {
        user1.appendText("${it.text}\n")
    }
}
//其他的BodyEvent是传递实体参数

3.发布消息

EventBus.publish(DefaultEvent(DemoId.RELOAD_UI))

EventBus.publish(TextEvent(DemoId.CHAT, sendText.text))
```

**一.视图容器(BaseView)**

> 内置了封装便捷功能

1.创建（注意rootPane不能为val）

- 通过fxml构建

> 外部类创建

```kotlin
GlobalResource.enableElement(true)
val view = BaseView.createView<BaseViewDemoView, AnchorPane>(xxx.fxml)
val scene = Scene(view.getRoot())
primaryStage.title = "Hello World"
primaryStage.scene = scene
primaryStage.show()
```
>BaseView子视图内创建
```kotlin
GlobalResource.enableElement(true)

//...

val view = createView<BaseViewDemoView, AnchorPane>(xxx.fxml)
val scene = Scene(view.getRoot())
primaryStage.title = "Hello World"
primaryStage.scene = scene
primaryStage.show()
```
- 直接传入RootPane(未完成)

2.消息总线
>接受者处理方法上用 @FXReceiver("test) 标注
> 
>发送者调用send()方法

3.弹出提示框

4.模态提示

5.模态通知

---

**二.表单视图**

> 自动组合表单元素和基础操作功能
>> 定义

```kotlin
class UserForm(t: UserData? = null) : FormUI<UserData>("用户管理", t) {}
```

> > 构建元素

buildElements()
元素跟字段数量、顺序一致

var

java.lang.IllegalArgumentException: argument type mismatch


> > 所有数据业务操作

datas()

> > 保存数据业务操作

saveData()

>>编辑数据业务操作  

editData()

>>删除数据业务操作  

delData()

>>使用
```kotlin
//1
val userForm = UserForm(UserData(1, "111111", SelectTypeEnum.C, true, "男"))
//2
val userForm = UserForm(UserData(1, "111111", SelectTypeEnum.C, true, "男"))
userForm.show()
```
- 文本
>data class
```kotlin
val id = TextElement("ID:", UserData::id, true)
val name = TextElement("名称:", UserData::name, isTextArea = true, primary = false)
```
>java bean
```kotlin
val id = TextElement<Person, Int>("ID：", DelegateProp("id"), primary = true)
```

- 下拉框
```kotlin
val type = SelectElement("类型:", UserData::type, SelectTypeEnum.values().toList())
```
- 勾选框
```kotlin
val child = CheckElement("是否未成年:", UserData::child)
```
- 单选组
```kotlin
val sex = RadioElement("性别:", UserData::sex, listOf("男", "女", "中间"))
```
**三.表格视图**
>TableView

- 可编辑单元格

*四.树视图*

>考虑到树有很多应用场景，业务过程中产生很多冗余代码

- 根据数据集（分List数据、Tree数据两种数据源）自动构建树节点，也可一键动态刷新
- 动态添加
- 动态删除
- 动态修改树节点

**四.样式按钮**
>必须先开启ElementUI

styleClass分别对应 `button button-primary button-success button-info button-warning button-danger`![image-20201014164041215](doc/image-20201014164041215.png)

```xml
<Button fx:id="viewBatchBtn" mnemonicParsing="false" onAction="#viewBatch" prefHeight="31.0"
                    prefWidth="97.0" styleClass="button-warn" text="查看参数"/>
```