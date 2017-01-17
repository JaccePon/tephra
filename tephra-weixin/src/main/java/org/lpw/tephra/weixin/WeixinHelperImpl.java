package org.lpw.tephra.weixin;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.crypto.Digest;
import org.lpw.tephra.scheduler.HourJob;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Generator;
import org.lpw.tephra.util.Http;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.lpw.tephra.weixin.gateway.PayGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
@Service("tephra.weixin.helper")
public class WeixinHelperImpl implements WeixinHelper, HourJob, ContextRefreshedListener {
    @Inject
    private Digest digest;
    @Inject
    private Http http;
    @Inject
    private Converter converter;
    @Inject
    private Generator generator;
    @Inject
    private Context context;
    @Inject
    private Validator validator;
    @Inject
    private Io io;
    @Inject
    private Logger logger;
    @Value("${tephra.ctrl.service-root:}")
    private String root;
    @Value("${tephra.weixin.mp.config:}")
    private String config;
    private Map<String, WeixinConfig> configs;
    private Map<String, PayGateway> gateways;

    @Override
    public String createQrCode(String appId, int id, int expire, String sceneStr) {
        if (id < 0) {
            logger.warn(null, "二维码ID[{}]小于0！", id);

            return null;
        }

        JSONObject object = new JSONObject();
        object.put("action_name", expire > 0 ? "QR_SCENE" : "QR_LIMIT_STR_SCENE");
        JSONObject info = new JSONObject();
        JSONObject scene = new JSONObject();
        if (expire > 0) {
            object.put("expire_seconds", expire);
            scene.put("scene_id", id);
        } else
            scene.put("scene_str", id > 0 ? id : sceneStr);
        info.put("scene", scene);
        object.put("action_info", info);

        JSONObject result = JSONObject.fromObject(http.post("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + getToken(appId), null, object.toString()));
        if (logger.isDebugEnable())
            logger.debug("创建微信二维码[{}]。", result);

        return result.has("ticket") ? ("https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + converter.encodeUrl(result.getString("ticket"), null)) : null;
    }

    @Override
    public String getRedirectUrl(String appId, String uri) {
        return new StringBuilder("https://open.weixin.qq.com/connect/oauth2/authorize?appid=").append(appId).append("&redirect_uri=")
                .append(converter.encodeUrl(root + WeixinService.URI + appId + "?redirect=" + uri, null)).append("&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect").toString();
    }

    @Override
    public JSONObject getUserInfo(String appId, String openId) {
        if (validator.isEmpty(openId))
            return null;

        Map<String, String> map = new HashMap<>();
        map.put("access_token", getToken(appId));
        map.put("openid", openId);
        map.put("lang", "zh_CN");

        return JSONObject.fromObject(http.get("https://api.weixin.qq.com/sns/userinfo", null, map));
    }

    @Override
    public JSONObject getJsApiSign(String appId, String url) {
        JSONObject object = new JSONObject();
        object.put("nonceStr", generator.random(32));
        object.put("timestamp", System.currentTimeMillis() / 1000);
        object.put("url", url);
        object.put("signature", digest.sha1("jsapi_ticket=" + getJsapiTicket(appId)
                + "&noncestr=" + object.getString("nonceStr") + "&timestamp=" + object.getLong("timestamp") + "&url=" + url));

        return object;
    }

    @Override
    public String upload(String appId, String type, String uri) {
        File file = new File(context.getAbsolutePath(uri));
        if (!file.exists()) {
            logger.warn(null, "要上传到微信临时素材区的媒体文件[{}]不存在！", uri);

            return null;
        }

        Map<String, File> map = new HashMap<>();
        map.put("media", file);
        JSONObject result = JSONObject.fromObject(http.upload("https://api.weixin.qq.com/cgi-bin/media/upload?access_token=" + getToken(appId) + "&type=" + type, null, null, map));
        if (result.has("errcode")) {
            logger.warn(null, "上传媒体文件[{}]到微信临时素材区失败[{}]！", uri, result);

            return null;
        }

        return result.getString("thumb".equals(type) ? "thumb_media_id" : "media_id");
    }

    @Override
    public String download(String appId, String mediaId, Timestamp time, boolean https) {
        File temp = new File(context.getAbsolutePath("/upload/weixin/" + generator.random(32)));
        Map<String, String> map = http.download((https ? "https" : "http") + "://api.weixin.qq.com/cgi-bin/media/get?access_token=" + getToken(appId) + "&media_id=" + mediaId, null, "", temp.getAbsolutePath());
        if (map == null) {
            temp.delete();

            return null;
        }

        String disposition = map.get("Content-disposition");
        String uri = new StringBuilder().append("/upload/").append(map.get("Content-Type")).append("/weixin/").append(converter.toString(time, "yyyyMMdd"))
                .append('/').append(temp.getName()).append(disposition.substring(disposition.lastIndexOf('.'), disposition.length() - 1)).toString();

        try {
            File file = new File(context.getAbsolutePath(uri));
            file.getParentFile().mkdirs();
            OutputStream output = new FileOutputStream(file);
            InputStream input = new FileInputStream(temp);
            io.copy(input, output);
            input.close();
            output.close();
            temp.delete();
        } catch (IOException e) {
            logger.warn(e, "移动文件时发生异常！");
        }

        return uri;
    }

    @Override
    public String getToken(String appId) {
        WeixinConfig config = configs.get(appId);

        return config == null ? null : config.getCurrentToken();
    }

    @Override
    public String getJsapiTicket(String appId) {
        WeixinConfig config = configs.get(appId);

        return config == null ? null : config.getJsapiTicket();
    }

    @Override
    public void executeHourJob() {
        refreshToken();
    }

    @Override
    public PayGateway getPayGateway(String type) {
        if (!gateways.containsKey(type)) {
            logger.warn(null, "微信支付类型[{}]不存在！", type);

            return null;
        }

        return gateways.get(type);
    }

    @Override
    public WeixinConfig getConfig(String appId) {
        if (!configs.containsKey(appId)) {
            logger.warn(null, "无法获得微信[AppId:{}]配置！", appId);

            return null;
        }

        return configs.get(appId);
    }

    @Override
    public int getContextRefreshedSort() {
        return 31;
    }

    @Override
    public void onContextRefreshed() {
        if (validator.isEmpty(config))
            return;

        configs = new HashMap<>();
        JSONArray array = JSONArray.fromObject(config);
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            WeixinConfig config = new WeixinConfig();
            config.setAppId(object.getString("appId"));
            config.setSecret(object.getString("secret"));
            config.setToken(object.getString("token"));
            if (object.has("mchId"))
                config.setMchId(object.getString("mchId"));
            if (object.has("mchKey"))
                config.setMchKey(object.getString("mchKey"));
            configs.put(config.getAppId(), config);
        }
        refreshToken();
        if (logger.isDebugEnable())
            logger.debug("配置微信公众号{}完成。", configs);

        gateways = new HashMap<>();
        BeanFactory.getBeans(PayGateway.class).forEach(gateway -> gateways.put(gateway.getType(), gateway));
    }

    private void refreshToken() {
        if (validator.isEmpty(configs))
            return;

        configs.values().forEach(config -> {
            JSONObject object = JSONObject.fromObject(http.get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + config.getAppId() + "&secret=" + config.getSecret(), null, ""));
            if (object != null && object.has("access_token")) {
                config.setCurrentToken(object.getString("access_token"));
                if (logger.isInfoEnable())
                    logger.info("获取微信公众号Token[{}:{}]。", config.getAppId(), config.getCurrentToken());
            } else
                logger.warn(null, "获取微信公众号Token[{}]失败！", object);

            object = JSONObject.fromObject(http.get("https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi&access_token=" + config.getCurrentToken(), null, ""));
            if (object != null && object.has("ticket")) {
                config.setJsapiTicket(object.getString("ticket"));
                if (logger.isInfoEnable())
                    logger.info("获取微信公众号JSAPI Ticket[{}:{}]。", config.getAppId(), config.getCurrentToken());
            } else
                logger.warn(null, "获取微信公众号JSAPI Ticket[{}]失败！", object);
        });
    }
}
