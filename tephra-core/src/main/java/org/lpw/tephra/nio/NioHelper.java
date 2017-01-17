package org.lpw.tephra.nio;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author lpw
 */
public interface NioHelper {
    /**
     * 保存ChannelHandlerContext对象。
     *
     * @param context ChannelHandlerContext实例。
     * @return Session ID值。
     */
    String put(ChannelHandlerContext context);

    /**
     * 获取Session ID值。
     *
     * @param context ChannelHandlerContext实例。
     * @return Session ID值。
     */
    String getSessionId(ChannelHandlerContext context);

    /**
     * 读取数据。
     *
     * @param message 数据源。
     * @return 数据。
     */
    byte[] read(Object message);

    /**
     * 发送信息。
     *
     * @param sessionId Session ID值。
     * @param message   信息。
     */
    void send(String sessionId, byte[] message);
}
