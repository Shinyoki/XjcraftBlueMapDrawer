package org.xjcraft.senkosan.bluemap.utils;

/**
 * String工具类
 *
 * @author senko
 * @date 2022/8/14 12:42
 */
public class StringUtil {

    public static String toString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    public static boolean isNotBlank(String str) {
        return str != null && str.trim().length() > 0;
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

}
