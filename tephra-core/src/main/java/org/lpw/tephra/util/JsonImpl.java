package org.lpw.tephra.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Component("tephra.util.json")
public class JsonImpl implements Json {
    @Inject
    private Validator validator;
    @Inject
    private Xml xml;

    @Override
    public void addAsArray(JSONObject json, String key, Object object) {
        if (validator.isEmpty(key))
            return;

        if (json.has(key)) {
            json.accumulate(key, object);

            return;
        }

        JSONArray array = new JSONArray();
        array.add(object);
        json.put(key, array);
    }

    @Override
    public String[] getAsStringArray(JSONObject json, String key) {
        if (json == null || validator.isEmpty(key) || !json.has(key))
            return new String[0];

        JSONArray array = json.getJSONArray(key);
        String[] strings = new String[array.size()];
        for (int i = 0; i < strings.length; i++)
            strings[i] = array.getString(i);

        return strings;
    }

    @Override
    public JSONObject fromXml(String xml) {
        return this.xml.toJson(xml);
    }

    @Override
    public <T> T get(JSONObject json, String key) {
        return null;
    }
}
