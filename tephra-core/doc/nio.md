# NIO Server & Client
Tephra提供了NIO支持，以简化Socket开发。

## Server
1、实现ServerListener接口：
```java
package org.lpw.tephra.nio;

/**
 * 服务端监听器。用于处理来自客户端的消息。
 *
 * @author lpw
 */
public interface ServerListener extends Listener {
    /**
     * 获得监听端口号。
     *
     * @return 端口号。
     */
    int getPort();

    /**
     * 获得最大处理线程数。
     *
     * @return 最大处理线程数。
     */
    int getMaxThread();

    /**
     * 接受新连接。当监听端口完成新Socket连接时，通知此方法。
     *
     * @param sessionId 客户端Session ID。
     */
    void accept(String sessionId);

    /**
     * 连接断开。当连接断开时回调此方法。
     *
     * @param sessionId 客户端Session ID。
     */
    void disconnect(String sessionId);
}
```
其中，Listener定义了接收到数据时的处理接口：
```java
package org.lpw.tephra.nio;

/**
 * 监听器，用于处理接收的数据。
 *
 * @author lpw
 */
public interface Listener {
    /**
     * 接收数据。当监听端口接收到数据时，通知此方法。
     *
     * @param sessionId 客户端Session ID值。
     * @param message   数据。
     */
    void receive(String sessionId, byte[] message);
}
```
2、当系统启动时，Tephra会自动启动监听端口，并在收到请求时通知监听服务。

3、当需要往客户端推送消息时，可以使用NioHelper：
```java
package org.lpw.tephra.nio;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author lpw
 */
public interface NioHelper {
    /**
     * 发送信息。
     *
     * @param sessionId Session ID值。
     * @param message   信息。
     */
    void send(String sessionId, byte[] message);
}
```
## Client
1、实现ClientListener接口：
```java
package org.lpw.tephra.nio;

/**
 * 客户端监听器。用于处理来自服务端的信息。
 *
 * @author lpw
 */
public interface ClientListener extends Listener {
    /**
     * 连接完成。当连接完成时回调此方法。
     */
    void connect(String sessionId);

    /**
     * 连接被断开。当连接被断开时回调此方法。
     */
    void disconnect();
}
```
2、获取一个Client实例：
```java
@Inject
protected ClientManager clientManager;
 
Client client = clientManager.get();
```
3、连接远程服务：
```java
package org.lpw.tephra.nio;

/**
 * 连接客户端。
 *
 * @author lpw
 */
public interface Client {
    /**
     * 连接服务端。
     *
     * @param listener 监听器。
     * @param ip       服务端IP地址。
     * @param port     服务端端口号。
     */
    void connect(ClientListener listener, String ip, int port);

    /**
     * 关闭连接。
     */
    void close();
}
```
4、通过NioHelper推送信息。

5、clientManager.close()可以关闭所有客户端连接。