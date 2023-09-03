# LerverUI 自定义javaFX框架吉快捷操作(kotlin)

---

部分功能(模态窗口提示、弹出通知、样式)引用和借鉴了[easyfx](https://github.com/xizi110/easyfx)。

---
### 引入

```
<dependency>
    <groupId>cn.lifay</groupId>
    <artifactId>LerverUI</artifactId>
    <version>1.12-SNAPSHOT</version>
</dependency>

<repositories>
    <repository>
        <id>gitee-repo</id>
        <name>The Maven Repository on Gitee</name>
         <url>https://lifay.gitee.io/lerverui/repodir/</url>
     </repository>
</repositories>
```
**一.基础视图**

>封装便捷功能

1.创建（注意rootPane不能为val）

- 通过fxml构建
>外部类创建

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