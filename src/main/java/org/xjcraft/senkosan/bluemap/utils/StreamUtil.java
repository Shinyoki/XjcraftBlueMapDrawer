package org.xjcraft.senkosan.bluemap.utils;

import java.io.InputStream;

/**
 * 流处理Util
 *
 * @author senko
 * @date 2023/2/4 12:39
 */
public class StreamUtil {

    /**
     * 关闭流
     * @param closeables    流...
     */
    public static void close(AutoCloseable... closeables) {
        for (AutoCloseable closeable : closeables) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * IS转换为String
     * @param inputStream   输入流
     * @return              String
     */
    public static String read(InputStream inputStream) {
        try {
            byte[] buffer = new byte[1024];
            int len;
            StringBuilder sb = new StringBuilder();
            while ((len = inputStream.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, len));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
