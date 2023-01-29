package cn.lifay;

import javafx.application.Application;

/**
 * Startup 启动类
 * @author lifay
 * @date  2023/1/9 16:07
 **/
public class Startup {

    public static void main(String[] args) {
        System.out.println(Math.sqrt(4));
        System.out.println(Math.sqrt(5));
        System.out.println(Math.sqrt(6));
        System.out.println(Math.sqrt(7));
        System.out.println(Math.sqrt(8));
        System.out.println(Math.sqrt(9));
        Application.launch(Demo.class);

    }
}