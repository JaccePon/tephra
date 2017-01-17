package org.lpw.tephra.util;

import java.util.Locale;

/**
 * 运行上下文。
 *
 * @author lpw
 */
public interface Context {
    /**
     * 获取绝对路径。
     *
     * @param path 相对路径。
     * @return 绝对路径。
     */
    String getAbsolutePath(String path);

    /**
     * 设置本地化信息。仅当前线程有效。
     *
     * @param locale 本地化信息。
     */
    void setLocale(Locale locale);

    /**
     * 获取本地化信息。
     *
     * @return 本地化信息；如果未设置则使用默认值。
     */
    Locale getLocale();
}
