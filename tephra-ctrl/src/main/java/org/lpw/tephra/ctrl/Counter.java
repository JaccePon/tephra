package org.lpw.tephra.ctrl;

/**
 * @author lpw
 */
public interface Counter {
    /**
     * 增加访问计数。
     *
     * @param ip 请求方IP地址。
     * @return 如果允许访问则返回true；否则返回false。
     */
    boolean increase(String ip);

    /**
     * 获取当前请求总数。
     *
     * @return 当前请求总数。
     */
    int get();

    /**
     * 减少访问计数。
     *
     * @param ip 请求方IP地址。
     */
    void decrease(String ip);
}
