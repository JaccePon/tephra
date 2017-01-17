package org.lpw.tephra.weixin;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author lpw
 */
public class NewsBuilder {
    private JSONArray array;

    public NewsBuilder() {
        array = new JSONArray();
    }

    /**
     * 添加新闻。
     *
     * @param title       新闻标题。
     * @param description 新闻描述。
     * @param url         目标URL地址。
     * @param imageUrl    图片URL地址。
     * @return 当前实例；如果新闻数量达到8条时，将放弃添加并返回null。
     */
    public NewsBuilder add(String title, String description, String url, String imageUrl) {
        if (array.size() > 7)
            return null;

        JSONObject object = new JSONObject();
        object.put("title", title);
        object.put("description", description);
        object.put("url", url);
        object.put("picurl", imageUrl);
        array.add(object);

        return this;
    }

    /**
     * 获取新闻集。
     *
     * @return 新闻集。
     */
    JSONArray getArray() {
        return array;
    }
}
