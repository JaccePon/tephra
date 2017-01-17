package org.lpw.tephra.cache.lr;

/**
 * 远程缓存客户端通道。
 *
 * @author lpw
 */
public interface Channel {
    enum State {
        Disconnect, Connected, Self
    }

    /**
     * 设置远程缓存服务端IP地址。
     *
     * @param ip 远程缓存服务端IP地址。
     */
    void setIp(String ip);

    /**
     * 建立连接。
     */
    void connect();

    /**
     * 获取连接Session ID值。
     *
     * @return 连接Session ID值。
     */
    String getSessionId();

    /**
     * 获取连接状态。
     *
     * @return 连接状态。
     */
    State getState();
}
