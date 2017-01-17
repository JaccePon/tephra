package org.lpw.tephra.weixin.gateway;

import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author lpw
 */
@Service("tephra.weixin.gateway.jsapi")
public class JsapiPayGatewayImpl extends PayGatewaySupport {
    @Override
    public String getType() {
        return JSAPI;
    }

    @Override
    protected void unifiedorder(Map<String, String> map, String openId) {
        map.put("device_info", "WEB");
        map.put("openid", openId);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    protected JSONObject prepay(String mpId, String timestamp, String nonce, String prepay) {
        JSONObject json = new JSONObject();
        json.put("appId", mpId);
        json.put("timeStamp", timestamp);
        json.put("nonceStr", nonce);
        json.put("package", "prepay_id=" + prepay);
        json.put("signType", "MD5");
        json.put("paySign", sign(json, weixinHelper.getConfig(mpId).getMchKey()));

        return json;
    }
}
