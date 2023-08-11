package cn.lifay.exception;

/**
 * LerverUIException 自定义异常
 *
 * @author lifay
 * @date 2023/1/8 10:05
 **/
public class LerverUIException extends RuntimeException {
    public LerverUIException(String message) {
        super("UI error: " + message);
    }
}
