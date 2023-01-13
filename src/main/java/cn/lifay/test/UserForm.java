package cn.lifay.test;

import cn.lifay.ui.form.FormUI;
import cn.lifay.ui.form.FormElement;
import cn.lifay.ui.form.select.EnumSelectElement;
import cn.lifay.ui.form.select.StringSelectElement;
import cn.lifay.ui.form.text.IntegerTextElement;
import cn.lifay.ui.form.text.StringTextElement;

import java.util.Arrays;
import java.util.List;

/**
 *@ClassName UserForm
 *@Description TODO
 *@Author 李方宇
 *@Date 2023/1/9 16:17
 **/
public class UserForm extends FormUI<User> {
    public UserForm(String title, Class<User> clazz) {
        super(title, clazz);
    }

    public UserForm(String title, User entity) {
        super(title, entity);
    }

    @Override
    public List<FormElement<User, ?>> buildElements() {
        IntegerTextElement<User> id = new IntegerTextElement<>("ID:", "id",true);
        StringTextElement<User> name = new StringTextElement<>("名称:", "name");
        EnumSelectElement<User,SelectTypeEnum> type = new EnumSelectElement<>("类型:", "type", Arrays.stream(SelectTypeEnum.values()).toList());

        return Arrays.asList(id, name,type);
    }


    @Override
    public void saveData(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            throw new RuntimeException("名称不能为空!");
        }
        System.out.println("保存数据操作:" + user.getId() + "  " + user.getName()+ "  " + user.getType());
    }

    @Override
    public void editData(User user) {
        if (user.getId() == null) {
            throw new RuntimeException("主键值不能为空!");
        }
        System.out.println("修改数据操作:" + user.getId() + "  " + user.getName()+ "  " + user.getType());
    }

    @Override
    public void delData(Object primaryValue) {
        checkId(primaryValue);
        System.out.println("删除数据操作:" + primaryValue);
    }
/*

    public Type getType(Object o){
        ParameterizedType parameterizedType = (ParameterizedType) o.getClass().getGenericSuperclass();
        System.out.println(Arrays.toString(parameterizedType.getActualTypeArguments()));
        return null;
    }
*/

}