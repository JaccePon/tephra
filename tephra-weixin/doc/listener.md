# 监听微信公众号信息与事件通知
1、设置微信监听服务为http(s)://${host}/tephra/weixin/${app-id}。其中${host}为服务域名，${app-id}为要监听的微信公众号app id。

2、实现WeixinListener接口，即可启用对微信公众号推送的信息与事件的监听：
```java
package org.lpw.tephra.weixin;

import java.sql.Timestamp;
import java.util.Map;

/**
 * 微信公众号监听器。用于监听微信公众号推送的信息与事件通知。
 *
 * @author lpw
 */
public interface WeixinListener {
    /**
     * 微信用户关注（订阅）公众号。
     *
     * @param mpId       公众号ID。
     * @param userOpenId 微信用户OpenID。
     * @param key        二维码参数，如果不存在则为null。
     * @param ticket     二维码的Ticket。
     * @param time       创建时间。
     */
    void subscribe(String mpId, String userOpenId, String key, String ticket, Timestamp time);

    /**
     * 微信用户取消关注（订阅）公众号。
     *
     * @param mpId       公众号ID。
     * @param userOpenId 微信用户OpenID。
     * @param time       创建时间。
     */
    void unsubscribe(String mpId, String userOpenId, Timestamp time);

    /**
     * 接收到文本信息。
     *
     * @param mpId       公众号ID。
     * @param userOpenId 微信用户OpenID。
     * @param messageId  消息ID。
     * @param content    文本信息。
     * @param time       创建时间。
     */
    void text(String mpId, String userOpenId, String messageId, String content, Timestamp time);

    /**
     * 接收到图片信息。
     *
     * @param mpId       公众号ID。
     * @param userOpenId 微信用户OpenID。
     * @param messageId  消息ID。
     * @param uri        图片URI地址。
     * @param time       创建时间。
     */
    void image(String mpId, String userOpenId, String messageId, String uri, Timestamp time);

    /**
     * 接收到图片信息。
     *
     * @param mpId       公众号ID。
     * @param userOpenId 微信用户OpenID。
     * @param messageId  消息ID。
     * @param format     语音格式。
     * @param uri        语音URI地址。
     * @param time       创建时间。
     */
    void voice(String mpId, String userOpenId, String messageId, String format, String uri, Timestamp time);

    /**
     * 接收到视频信息。
     *
     * @param mpId       公众号ID。
     * @param userOpenId 微信用户OpenID。
     * @param messageId  消息ID。
     * @param uri        视频URI地址。
     * @param thumbnail  缩略图URI地址。
     * @param time       创建时间。
     */
    void video(String mpId, String userOpenId, String messageId, String uri, String thumbnail, Timestamp time);

    /**
     * 接收到地理位置信息。
     *
     * @param mpId       公众号ID。
     * @param userOpenId 微信用户OpenID。
     * @param messageId  消息ID。
     * @param x          X坐标值。
     * @param y          Y坐标值。
     * @param scale      地图缩放大小。
     * @param label      地理位置信息。
     * @param time       创建时间。
     */
    void location(String mpId, String userOpenId, String messageId, double x, double y, int scale, String label, Timestamp time);

    /**
     * 接收到二维码被扫描信息。
     *
     * @param mpId       公众号ID。
     * @param userOpenId 微信用户OpenID。
     * @param key        二维码scene_id。
     * @param ticket     二维码的Ticket。
     * @param time       创建时间。
     */
    void scan(String mpId, String userOpenId, String key, String ticket, Timestamp time);

    /**
     * 接收到菜单单击事件。
     *
     * @param mpId       公众号ID。
     * @param userOpenId 微信用户OpenID。
     * @param key        菜单key值。
     * @param time       创建时间。
     */
    void click(String mpId, String userOpenId, String key, Timestamp time);

    /**
     * 接收到菜单单击跳转页面事件。
     *
     * @param mpId       公众号ID。
     * @param userOpenId 微信用户OpenID。
     * @param url        跳转URL地址。
     * @param time       创建时间。
     */
    void redirect(String mpId, String userOpenId, String url, Timestamp time);

    /**
     * 微信支付回调。
     * 仅在充值成功时回调。
     *
     * @param orderNo    订单号。
     * @param wxOrderNo  微信订单号。
     * @param parameters 微信回调参数集。
     */
    void pay(String orderNo, String wxOrderNo, Map<String, String> parameters);
}
```
微信监听采用异步处理机制，即在接收到微信推送时，将推送的信息加入到处理队列中，但不等待处理结果立即返回；这是因为微信要求在5秒内返回。