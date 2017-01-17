package org.lpw.tephra.test;

import org.lpw.tephra.ctrl.context.HeaderAdapter;

import java.util.Map;

/**
 * @author lpw
 */
public interface MockHeader extends HeaderAdapter {
    /**
     * 设置IP地址。
     *
     * @param ip IP地址。
     */
    void setIp(String ip);

    /**
     * 设置请求头集。
     *
     * @param map 请求头集。
     */
    void setMap(Map<String, String> map);
}
