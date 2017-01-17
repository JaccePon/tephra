# 使用WebSocket
CtrlHttp模块提供了一个标准的WebSocket监听服务，可以在客户端建立与/tephra/ctrl-http/ws的通信，如：
```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>WebSocket</title>
</head>
<body>
</body>
<script type="text/javascript">
    var ws;
    var i = 0;
 
    function send() {
        ws.send("hello ws " + i++);
 
        if (i > 9) {
            ws.close();
 
            return;
        }
 
        setTimeout(send, 1000);
    }
 
    if (window.WebSocket) {
        ws = new WebSocket("ws://localhost:8080/tephra/ctrl-http/ws");
        ws.onopen = function (event) {
            console.log("open");
        };
 
        ws.onmessage = function (event) {
            console.log("receive:" + event.data);
        };
 
        ws.onclose = function (event) {
            console.log("close");
        };
 
        setTimeout(send, 1000);
    }
    else
        alert("WebSocket not support !");
</script>
</html>
```
服务端需要提供一个WsListener实现用于接收数据（如果未提供则不启用WebSocket监听），接口描述如下：
```java
package org.lpw.tephra.ctrl.http.ws;

/**
 * WebSocket监听器。
 *
 * @author lpw
 */
public interface WsListener {
    /**
     * 处理连接打开事件。
     *
     * @param session 连接Session ID。
     */
    void open(String session);

    /**
     * 处理接收到新消息事件。
     *
     * @param session 连接Session ID。
     * @param message 消息。
     */
    void message(String session, String message);

    /**
     * 处理连接异常事件。
     *
     * @param session   连接Session ID。
     * @param throwable 异常。
     */
    void error(String session, Throwable throwable);

    /**
     * 处理连接关闭事件。
     *
     * @param session 连接Session ID。
     */
    void close(String session);
}
```
示例参考：
```java
package org.lpw.tephra.ctrl.http.ws;
 
import org.springframework.stereotype.Service;

import javax.inject.Inject;
 
/**
 * @author lpw
 */
@Service("tephra.ctrl.http.ws.listener")
public class WsListenerImpl implements WsListener {
    @Inject
    private WsHelper wsHelper;
 
    @Override
    public void open(String session) {
        wsHelper.send(session, "hello");
        System.out.println("open");
    }
 
    @Override
    public void message(String session, String message) {
        wsHelper.send(session, "receive:" + message);
        System.out.println("receive:" + message);
    }
 
    @Override
    public void error(String session, Throwable throwable) {
    }
 
    @Override
    public void close(String session) {
        System.out.println("close");
    }
}
```
可以借助WsHelper对WebSocket进行操作：
```java
package org.lpw.tephra.ctrl.http.ws;

import javax.websocket.Session;

/**
 * WebSocket支持类。
 *
 * @author lpw
 */
public interface WsHelper {
    /**
     * WebSocket监听URI地址。
     */
    String URI = "/tephra/ctrl-http/ws";

    /**
     * 处理连接打开事件。
     *
     * @param session 连接Session。
     */
    void open(Session session);

    /**
     * 处理接收到新消息事件。
     *
     * @param session 连接Session。
     * @param message 消息。
     */
    void message(Session session, String message);

    /**
     * 处理连接异常事件。
     *
     * @param session   连接Session。
     * @param throwable 异常。
     */
    void error(Session session, Throwable throwable);

    /**
     * 处理连接关闭事件。
     *
     * @param session 连接Session。
     */
    void close(Session session);

    /**
     * 发送消息到客户端。
     *
     * @param session 客户端Session ID。
     * @param message 消息。
     */
    void send(String session, String message);

    /**
     * 发送消息到所有客户端。
     *
     * @param message 消息。
     */
    void send(String message);

    /**
     * 关闭客户端连接。
     *
     * @param session 客户端Session ID。
     */
    void close(String session);

    /**
     * 关闭所有客户端连接。
     */
    void close();
}
```
可以通过修改http.ctrl.tephra.config.properties设置最大允许连接数：
```properties
## 设置WebSocket最大连接客户端数，超过此设置将不再建立新的连接。
#tephra.ctrl.http.web-socket.max = 64
```