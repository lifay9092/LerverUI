package cn.lifay.ui.form

import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.HBox
import java.util.function.Predicate
import kotlin.reflect.KMutableProperty1

/**
 * @ClassName FormElement
 * @Description TODO
 * @Author lifay
 * @Date 2023/1/8 10:09
 */
abstract class FormElement<T, R : Any>(
    val r: Class<R>,
    val label: String,
    val property: KMutableProperty1<T, R>,
    var primary: Boolean = false
) :
    HBox() {

    private var control: Control? = null
/*
    var property: KMutableProperty1<T, R>? = null

    var isPrimary = false*/

    private val predicate: Predicate<R>? = null


/*    protected constructor(label: String, property: KMutableProperty1<T, R>, primary: Boolean = false) {
        alignment = Pos.CENTER_LEFT
        val l = Label(label)
        l.padding = Insets(5.0, 10.0, 5.0, 10.0)
        children.add(l)
        padding = Insets(5.0, 10.0, 5.0, 10.0)
        this.property = property
        isPrimary = primary
    }*/

    fun init() {

        println("R: ${r}")
        alignment = Pos.CENTER_LEFT
        val l = Label(label)
        l.padding = Insets(5.0, 10.0, 5.0, 10.0)
        children.add(l)
        padding = Insets(5.0, 10.0, 5.0, 10.0)
        control = control()
        this.elementValue = initControlValue() as R?
        children.add(control)
    }

    abstract fun control(): Control?
    var elementValue: R?
        get() {
            return when (control) {
                is TextField, is TextArea -> {
                    val text = (control as TextField).text
                    when (r) {
                        is String -> {}
                        else -> {}
                    }
                }
                is ChoiceBox<*> -> {
                    (control as ChoiceBox<*>).value
                }
                is CheckBox -> {
                    (control as CheckBox).isSelected
                }
                else -> {
                    null
                }
            } as R?
        }
        set(value) {
            when (control) {
                is TextField -> {
                    (control as TextField).text = value.toString()
                }
                is TextArea -> {
                    (control as TextArea).text = value.toString()
                }
                is ChoiceBox<*> -> {
                    (control as ChoiceBox<*>).value = value
                }
                is CheckBox -> {
                    (control as CheckBox).isSelected = value as Boolean
                }
                else -> {

                }
            }
        }
    /*
    var elementValue: R? by Delegates.observable(initControlValue() as R? ) {
            prop, old, new ->
        println("$old -> $new")
        when (control) {
            is TextField  -> {
                (control as TextField).text = new.toString()}
            is TextArea  -> {
                (control as TextArea).text = new.toString()}
            is ChoiceBox<*> -> {
                (control as ChoiceBox<*>).value = new}
            is CheckBox -> {
                (control as CheckBox).isSelected = new as Boolean}
            else -> {

            }
        }
    }*/

    private fun initControlValue(): Any? {
        return when (control) {
            is TextField, is TextArea -> {
                ""
            }
            is CheckBox -> {
                false
            }
            else -> {
                null
            }
        }
    }

    protected fun verify(): Boolean {
        return predicate?.test(elementValue!!) ?: true
    }

    fun disable() {
        Platform.runLater { control!!.isDisable = true }
    }

    fun clear() {
        Platform.runLater {
            if (control is TextField) {
                (control as TextField).clear()
                if (primary) {
                    (control as TextField).setDisable(false)
                    //System.out.println(control.isDisable());
                }
            } else if (control is ChoiceBox<*>) {
                (control as ChoiceBox<*>).setValue(null)
            }
        }
    }

    fun setEle(t: T) {
        val get = property.get(t)
        this.elementValue = get

    }

    fun setProp(t: T) {
        property.set(t, elementValue as R)
    }

}