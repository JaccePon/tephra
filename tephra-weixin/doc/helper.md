# 使用WeixinHelper开发微信功能
WeixinHelper提供了Token的管理与获取，每个小时会自动更新一次Token。
```java
package org.lpw.tephra.weixin;

import net.sf.json.JSONObject;
import org.lpw.tephra.weixin.gateway.PayGateway;

import java.sql.Timestamp;

/**
 * 微信支持类。
 * 提供微信操作接口。
 *
 * @author lpw
 */
public interface WeixinHelper {
    /**
     * 创建微信二维码图片。
     *
     * @param appId  微信公众号AppID。
     * @param id     场景ID，正整数。
     * @param expire 有效期，单位：秒。大于0则表示创建临时二维码，其TA则创建永久二维码。
     * @param scene  场景参数，仅当创建永久二维码、并且id为0时有效。
     * @return 如果创建成功则返回二维码图片地址；否则返回null。
     */
    String createQrCode(String appId, int id, int expire, String scene);

    /**
     * 获取重定向URL地址。
     *
     * @param appId 微信公众号AppID。
     * @param uri   目标URI地址。
     * @return 重定向URL地址。
     */
    String getRedirectUrl(String appId, String uri);

    /**
     * 获取用户信息。
     *
     * @param appId  微信公众号AppID。
     * @param openId 微信用户OpenID。
     * @return 用户信息集；如果用户未关注公众号或获取失败则返回null。
     */
    JSONObject getUserInfo(String appId, String openId);

    /**
     * 上传媒体文件到微信临时素材区。
     *
     * @param appId 微信公众号AppID。
     * @param type  文件类型，可选值为：图片（image）、语音（voice）、视频（video）和缩略图（thumb）。
     * @param uri   媒体文件URI地址。
     * @return 如果上传成功则返回MediaID，否则返回null。
     */
    String upload(String appId, String type, String uri);

    /**
     * 下载媒体文件。
     *
     * @param appId   微信公众号AppID。
     * @param mediaId 媒体ID。
     * @param time    媒体创建时间。
     * @param https   是否使用HTTPS协议。
     * @return 文件URI地址。
     */
    String download(String appId, String mediaId, Timestamp time, boolean https);

    /**
     * 获取微信公众号API交互Token。
     *
     * @param appId 微信公众号AppID。
     * @return 微信公众号API交互Token。
     */
    String getToken(String appId);

    /**
     * 获取微信公众号JSAPI Ticket。
     *
     * @param appId 微信公众号AppID。
     * @return 微信公众号JSAPI Ticket。
     */
    String getJsapiTicket(String appId);

    /**
     * 获取支付网关。
     *
     * @param type 支付网关类型。
     * @return 支付网关。
     */
    PayGateway getPayGateway(String type);

    /**
     * 获取微信公众号配置。
     *
     * @param appId 微信公众号AppID。
     * @return 微信公众号配置。
     */
    WeixinConfig getConfig(String appId);
}
```