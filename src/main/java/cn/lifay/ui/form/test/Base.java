package cn.lifay.ui.form.test;

import cn.hutool.json.JSONUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 *@ClassName Base
 *@Description TODO
 *@Author lifay
 *@Date 2023/1/10 21:11
 **/
abstract class Base<T extends Comparable<T>> {

    T data;

    public Base(String json) {
        this.data = JSONUtil.toBean(json, deSerializable());
    }

    private Class<T> deSerializable() {
        Type type = getClass().getGenericSuperclass();
        System.out.println("type:" + type.getTypeName());
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            System.out.println(parameterizedType.getActualTypeArguments()[0]);
            return (Class<T>) parameterizedType.getActualTypeArguments()[0];
        }
        throw new RuntimeException();
    }
}