# 使用JSAPI发起支付请求

## 使用PayGateway

需要在微信公众号里发起支付请求时，可以通过WeixinHelper获取支付网关：
```java
PayGateway gateway = weixinHelper.getPayGateway(PayGateway.JSAPI);
```
然后调用prepay获取JSAPI支付所需参数。PayGateway接口描述如下：
```java
package org.lpw.tephra.weixin.gateway;

import java.util.Map;

/**
 * 微信支付网关。
 *
 * @author lpw
 */
public interface PayGateway {
    /**
     * 微信公众号支付类型。
     */
    String JSAPI = "JSAPI";

    /**
     * 获取网关类型。
     *
     * @return 网关类型。
     */
    String getType();

    /**
     * 发起预支付请求。
     *
     * @param appId    微信公众号AppID。
     * @param openId  微信用户OpenID。
     * @param orderNo 订单号。
     * @param body    订单内容。
     * @param amount  金额，单位：分。
     * @return 支付参数；如果发起失败则返回null。
     */
    String prepay(String appId, String openId, String orderNo, String body, int amount);

    /**
     * 支付回调。
     *
     * @param parameters 参数集。
     * @return 返回结果。
     */
    void callback(Map<String, String> parameters);
}
```
> 支付完成后会回调给WeixinListener实现。

## 使用API服务

请求/tephra/weixin/prepay接口，参数描述如下：
- type 充值类型，[JSAPI]。
- appId 微信公众号AppID。
- orderNo 订单号。
- body 订单内容。
- amount 金额，单位：分。

如果创建成功，则返回发起微信支付所需的参数。

> 支付完成后会回调给WeixinListener实现。

## 设置回调URL根
```properties
## 设置服务URL根路径。如：
## * http://localhost
## * https://localhost
## * http://localhost:8080
#tephra.ctrl.service-root = 
```
> 微信支付完成后，将通知（回调）${tephra.ctrl.service-root}/tephra/weixin/jsapi。
