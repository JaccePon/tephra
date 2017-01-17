# 使用ModelHelper管理Model
ModelHelper提供了对Model的管理能力，包含属性的设置与获取、JSON对象转化、拷贝等功能。
```java
package org.lpw.tephra.dao.model;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Collection;

/**
 * Model支持类。用于操作Model属性。
 *
 * @author lpw
 */
public interface ModelHelper {
    /**
     * 获取属性值。
     *
     * @param model Model实例。
     * @param name  属性名称。可以是属性名，也可以是字段名。
     * @return 属性值。如果不存在则返回null。
     */
    Object get(Model model, String name);

    /**
     * 设置属性值。
     *
     * @param model Model实例。
     * @param name  属性名称。可以是属性名，也可以是字段名。
     * @param value 属性值。
     */
    void set(Model model, String name, Object value);

    /**
     * 将Model转化为JSON格式的数据。
     * 转化时将调用所有get方法输出属性值。
     *
     * @param model Model实例。
     * @param <T>   Model类型。
     * @return JSON数据。
     */
    <T extends Model> JSONObject toJson(T model);

    /**
     * 将Model集转化为JSON格式的数据集。
     * 转化时将调用所有get方法输出属性值。
     *
     * @param models Model集。
     * @param <T>    Model类型。
     * @return JSON数据集。
     */
    <T extends Model> JSONArray toJson(Collection<T> models);

    /**
     * 将JSON对象转化为Model对象。
     *
     * @param json       JSON对象。
     * @param modelClass Model类。
     * @return Model对象；如果转化失败则返回null。
     */
    <T extends Model> T fromJson(JSONObject json, Class<T> modelClass);

    /**
     * 复制Model属性。
     *
     * @param source    复制源。
     * @param target    目标。
     * @param containId 是否复制ID值。
     * @param <T>       Model类。
     */
    <T extends Model> void copy(T source, T target, boolean containId);
}
```