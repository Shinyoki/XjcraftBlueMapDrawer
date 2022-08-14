package org.xjcraft.senkosan.bluemap.exception;

/**
 * 异常基类
 *
 * @author senko
 * @date 2022/8/13 14:32
 */
public class XBMPluginException extends RuntimeException{

    public XBMPluginException(String message) {
        super(message);
    }

    public XBMPluginException(String message, Throwable cause) {
        super(message, cause);
    }

}
