package org.lpw.tephra.ctrl;

/**
 * IP白名单。
 *
 * @author lpw
 */
public interface TrustfulIp {
    /**
     * 验证IP地址是否在白名单中。
     *
     * @param ip IP地址。
     * @return 如果是则返回true，否则返回false。
     */
    boolean contains(String ip);
}
