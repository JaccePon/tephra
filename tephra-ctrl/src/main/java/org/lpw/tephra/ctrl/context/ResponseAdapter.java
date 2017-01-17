package org.lpw.tephra.ctrl.context;

import java.io.OutputStream;

/**
 * 输出适配器。
 *
 * @author lpw
 */
public interface ResponseAdapter {
    /**
     * 设置类容类型。
     *
     * @param contentType 类容类型。
     */
    void setContentType(String contentType);

    /**
     * 设置头信息。
     *
     * @param name  名称。
     * @param value 值。
     */
    void setHeader(String name, String value);

    /**
     * 获取输出流。
     *
     * @return 输出流。
     */
    OutputStream getOutputStream();

    /**
     * 跳转到指定URL地址。
     *
     * @param url 目标URL地址。
     */
    void redirectTo(String url);

    /**
     * 发送错误码。
     *
     * @param code 错误码。
     */
    void sendError(int code);
}
