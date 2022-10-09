package cn.lifay.exception;

/**
 *@ClassName LerverUIException
 *@Description 自定义异常
 *@Author lifay
 *@Date 2022/10/9 20:26
 **/
public class LerverUIException extends RuntimeException {
    public LerverUIException(String message) {
        super("LerverUI error >> " + message);
    }
}
