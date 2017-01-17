package org.lpw.tephra.carousel;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.util.Validator;

import java.util.Map;

/**
 * Action配置构造器。
 *
 * @author lpw
 */
public class ActionBuilder {
    protected Validator validator;
    protected JSONArray array;

    public ActionBuilder() {
        validator = BeanFactory.getBean(Validator.class);
        array = new JSONArray();
    }

    /**
     * 添加Action配置。
     *
     * @param name    名称。
     * @param handler 处理器。
     * @return 当前ActionBuilder实例。
     */
    public ActionBuilder add(String name, String handler) {
        return add(name, handler);
    }

    /**
     * 添加Action配置。
     *
     * @param name      名称。
     * @param handler   处理器。
     * @param parameter 参数。
     * @return 当前ActionBuilder实例。
     */
    public ActionBuilder add(String name, String handler, Map<String, String> parameter) {
        if (validator.isEmpty(name) || validator.isEmpty(handler))
            throw new NullPointerException("name或handler参数为空！");

        JSONObject object = new JSONObject();
        object.put("name", name);
        object.put("handler", handler);
        if (!validator.isEmpty(parameter))
            object.put("parameter", JSONObject.fromObject(parameter));

        return this;
    }

    /**
     * 获取Action配置集。
     *
     * @return Action配置集。
     */
    public JSONArray get() {
        if (array.isEmpty())
            throw new NullPointerException("Action配置为空！");

        return array;
    }
}
