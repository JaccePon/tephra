package org.lpw.tephra.weixin;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.lpw.tephra.util.Http;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Service("tephra.weixin.sender")
public class WeixinSenderImpl implements WeixinSender {
    @Inject
    private Validator validator;
    @Inject
    private Http http;
    @Inject
    private Logger logger;
    @Inject
    private WeixinHelper weixinHelper;

    @Override
    public boolean sendText(String mpId, String receiver, String text) {
        if (validator.isEmpty(receiver) || validator.isEmpty(text))
            return false;

        JSONObject object = new JSONObject();
        object.put("content", text);

        return send(mpId, receiver, "text", object);
    }

    @Override
    public boolean sendImage(String mpId, String receiver, String uri) {
        return send(mpId, receiver, "image", uri);
    }

    @Override
    public boolean sendVoice(String mpId, String receiver, String uri) {
        return send(mpId, receiver, "voice", uri);
    }

    private boolean send(String mpId, String receiver, String type, String uri) {
        if (validator.isEmpty(receiver) || validator.isEmpty(uri))
            return false;


        String mediaId = weixinHelper.upload(mpId, type, uri);
        if (validator.isEmpty(mediaId))
            return false;

        JSONObject object = new JSONObject();
        object.put("media_id", mediaId);

        return send(mpId, receiver, type, object);
    }

    @Override
    public boolean sendVideo(String mpId, String receiver, String uri, String thumbnail, String title, String description) {
        if (validator.isEmpty(receiver) || validator.isEmpty(uri) || validator.isEmpty(thumbnail))
            return false;

        String thumbnailMediaId = weixinHelper.upload(mpId, "thumb", thumbnail);
        if (validator.isEmpty(thumbnailMediaId))
            return false;

        String videoMediaId = weixinHelper.upload(mpId, "video", uri);
        if (validator.isEmpty(videoMediaId))
            return false;

        JSONObject object = new JSONObject();
        object.put("media_id", videoMediaId);
        object.put("thumb_media_id", thumbnailMediaId);
        object.put("title", title);
        object.put("description", description);

        return send(mpId, receiver, "video", object);
    }

    @Override
    public boolean sendNews(String mpId, String receiver, NewsBuilder builder) {
        if (validator.isEmpty(receiver))
            return false;

        JSONArray array = builder.getArray();
        if (array.isEmpty())
            return false;

        JSONObject object = new JSONObject();
        object.put("articles", array);

        return send(mpId, receiver, "news", object);
    }

    private boolean send(String mpId, String receiver, String type, JSONObject object) {
        JSONObject json = new JSONObject();
        json.put("touser", receiver);
        json.put("msgtype", type);
        json.put(type, object);
        JSONObject result = JSONObject.fromObject(http.post("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + weixinHelper.getToken(mpId), null, json.toString()));

        if (logger.isDebugEnable())
            logger.debug("发送[{}]消息[{}]到微信服务器[{}]。", type, json, result);

        return result.getInt("errcode") == 0;
    }
}
