package cn.lifay.ui.form.select;

import java.util.List;

/**
 *@ClassName StringSelectElement
 *@Description TODO
 *@Author 李方宇
 *@Date 2023/1/11 15:12
 **/
public class EnumSelectElement<T,R> extends SelectElement<T,R>{

    public EnumSelectElement(String label, String fieldName) {
        super(label, fieldName);
    }

    public EnumSelectElement(String label, String fieldName, List<R> items) {
        super(label, fieldName,items);
    }


}