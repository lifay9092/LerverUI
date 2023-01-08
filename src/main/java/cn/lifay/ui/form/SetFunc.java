package cn.lifay.ui.form;

/**
 *@ClassName SetFunc
 *@Description TODO
 *@Author lifay
 *@Date 2023/1/8 19:31
 **/
@FunctionalInterface
public interface SetFunc<T,R> {

    void set(T t,R r);
}
