package org.lpw.tephra.weixin.gateway;

import net.sf.json.JSONObject;
import org.lpw.tephra.crypto.Digest;
import org.lpw.tephra.ctrl.context.Header;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Generator;
import org.lpw.tephra.util.Http;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Xml;
import org.lpw.tephra.weixin.WeixinHelper;
import org.lpw.tephra.weixin.WeixinListener;
import org.lpw.tephra.weixin.WeixinService;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author lpw
 */
public abstract class PayGatewaySupport implements PayGateway {
    @Inject
    protected Generator generator;
    @Inject
    protected Converter converter;
    @Inject
    protected Http http;
    @Inject
    protected Xml xml;
    @Inject
    protected Digest digest;
    @Inject
    protected Logger logger;
    @Inject
    protected Header header;
    @Inject
    protected WeixinHelper weixinHelper;
    @Inject
    protected Optional<WeixinListener> weixinListener;
    @Value("${tephra.ctrl.service-root:}")
    protected String root;

    @Override
    public String prepay(String appId, String openId, String orderNo, String body, int amount) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", appId);
        map.put("mch_id", weixinHelper.getConfig(appId).getMchId());
        map.put("nonce_str", generator.random(32));
        map.put("body", body);
        map.put("out_trade_no", orderNo);
        map.put("total_fee", converter.toString(amount, "0"));
        map.put("spbill_create_ip", header.getIp());
        map.put("notify_url", root + WeixinService.URI + getType().toLowerCase());
        map.put("trade_type", getType());
        unifiedorder(map, openId);
        map.put("sign", sign(map, weixinHelper.getConfig(appId).getMchKey()));

        StringBuilder xml = new StringBuilder("<xml>");
        map.forEach((key, value) -> xml.append('<').append(key).append("><![CDATA[").append(value).append("]]></").append(key).append('>'));
        xml.append("</xml>");
        map = this.xml.toMap(http.post("https://api.mch.weixin.qq.com/pay/unifiedorder", null, xml.toString()), false);
        if (map == null || !"SUCCESS".equals(map.get("return_code")) || !"SUCCESS".equals(map.get("result_code"))) {
            logger.warn(null, "微信预支付[{}:{}]失败！", xml, map);

            return null;
        }

        String prepay = prepay(appId, converter.toString(System.currentTimeMillis() / 1000, "0"), map.get("nonce_str"), map.get("prepay_id")).toString();
        if (logger.isDebugEnable())
            logger.debug("返回预支付参数[{}]。", prepay);

        return prepay;
    }

    protected abstract void unifiedorder(Map<String, String> map, String openId);

    protected abstract JSONObject prepay(String mpId, String timestamp, String nonce, String prepay);

    @Override
    public void callback(Map<String, String> parameters) {
        if (logger.isDebugEnable())
            logger.debug("微信公众号支付结果回调[{}]。", converter.toString(parameters));

        if (!"SUCCESS".equals(parameters.get("return_code")) || !"SUCCESS".equals(parameters.get("result_code"))) {
            logger.warn(null, "微信公众号支付回调[{}]返回失败！", converter.toString(parameters));

            return;
        }

        Map<String, String> map = new HashMap<>(parameters);
        String sign = map.remove("sign");
        if (!sign(map, weixinHelper.getConfig(parameters.get("appid")).getMchKey()).equals(sign)) {
            logger.warn(null, "微信公众号支付回调签名认证[{}]失败！", converter.toString(parameters));

            return;
        }

        weixinListener.ifPresent(listener -> listener.pay(parameters.get("out_trade_no"), parameters.get("transaction_id"), parameters));
    }

    protected String sign(Map<String, String> map, String mchKey) {
        List<String> list = new ArrayList<>(map.keySet());
        Collections.sort(list);
        StringBuilder sb = new StringBuilder();
        list.forEach(key -> sb.append(key).append('=').append(map.get(key)).append('&'));
        sb.append("key=").append(mchKey);

        return digest.md5(sb.toString()).toUpperCase();
    }
}
