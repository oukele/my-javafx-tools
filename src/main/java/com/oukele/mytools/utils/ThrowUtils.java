package com.oukele.mytools.utils;

/**
 * 异常工具类
 *
 * @author oukele
 */
public class ThrowUtils {

    /**
     * 条件成立则抛异常
     *
     * @param condition        条件
     * @param runtimeException 异常
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition 条件
     * @param msg       异常信息
     */
    public static void throwIf(boolean condition, String msg) {
        throwIf(condition, new RuntimeException(msg));
    }

}
