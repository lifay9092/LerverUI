package cn.lifay.test;

/**
 *@ClassName User
 *@Description TODO
 *@Author 李方宇
 *@Date 2023/1/9 16:15
 **/
public class User {

    private Integer id;
    private String name;

    private SelectTypeEnum type;

    public User() {
    }

    public User(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SelectTypeEnum getType() {
        return type;
    }

    public void setType(SelectTypeEnum type) {
        this.type = type;
    }
}