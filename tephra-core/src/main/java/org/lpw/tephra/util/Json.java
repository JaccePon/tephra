package org.lpw.tephra.util;

import net.sf.json.JSONObject;

/**
 * @author lpw
 */
public interface Json {
    /**
     * 添加为数组。
     *
     * @param json   JSON对象。
     * @param key    属性key。
     * @param object 要添加的对象。
     */
    void addAsArray(JSONObject json, String key, Object object);

    /**
     * 获取字符串数组。
     *
     * @param json JSON对象。
     * @param key  属性key。
     * @return 字符串数组。
     */
    String[] getAsStringArray(JSONObject json, String key);

    /**
     * 将XML字符串转化为JSON对象。
     *
     * @param xml XML字符串。
     * @return JSON对象；如果转化失败则返回null。
     */
    JSONObject fromXml(String xml);

    /**
     * 获取JSON属性值。
     *
     * @param json JSON对象。
     * @param key  属性名称，点号表示上下级关系。
     * @param <T>  属性值类型。
     * @return 属性值。
     */
    <T> T get(JSONObject json, String key);
}
