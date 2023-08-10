package cn.lifay.test;

/**
 * Person TODO
 *
 * @author lifay
 * @date 2023/3/2 20:37
 **/
public class Person {

    private Integer id;
    private String name;
    private Boolean child;


    public Person(Integer id, String name, Boolean child) {
        this.id = id;
        this.name = name;
        this.child = child;
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

    public Boolean getChild() {
        return child;
    }

    public void setChild(Boolean child) {
        this.child = child;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", child=" + child +
                '}';
    }
}