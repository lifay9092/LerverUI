package cn.lifay.ui.form.test;

import cn.hutool.json.JSONUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 *@ClassName Test
 *@Description TODO
 *@Author lifay
 *@Date 2023/1/10 21:13
 **/
public class Test {

    public static void main(String[] args) {
        String json = JSONUtil.toJsonStr(new DataClass());
        Son s = new Son(json);
        Type t = s.getClass().getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            System.out.println(t);
            // output: cn.think.in.java.clazz.loader.generics.Base<cn.think.in.java.clazz.loader.generics.DataClass>
            for (Type type : ((ParameterizedType) t).getActualTypeArguments()) {
                System.out.println(type);
                //output: class cn.think.in.java.clazz.loader.generics.DataClass
            }
        }
    }
}
