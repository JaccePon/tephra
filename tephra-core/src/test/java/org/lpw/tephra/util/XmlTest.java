package org.lpw.tephra.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lpw.tephra.test.CoreTestSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author lpw
 */
public class XmlTest extends CoreTestSupport {
    @Inject
    private Xml xml;
    private String string = "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\"/>\n" +
            "    <title>Scala XML</title>\n" +
            "    <link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"/>\n" +
            "    <script type=\"text/javascript\" src=\"script.js\"></script>\n" +
            "</head>\n" +
            "<body>\n" +
            "<form method=\"post\">\n" +
            "    <input type=\"text\" name=\"username\" value=\"root\"/>\n" +
            "    <input type=\"password\" name=\"password\"/>\n" +
            "    <button id=\"sign-in\">Sign In</button>\n" +
            "</form>\n" +
            "</body>\n" +
            "</html>";

    @Test
    public void toJson() {
        Assert.assertNull(xml.toJson(null));
        Assert.assertNull(xml.toJson(""));
        toJson("");
        toJson("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    }

    private void toJson(String prefix) {
        JSONObject json = xml.toJson(prefix + string);
        Assert.assertNotNull(json);
        toJsonKeys(json, new String[]{"html"});
        JSONObject html = json.getJSONObject("html");
        toJsonKeys(html, new String[]{"lang", "head", "body"});
        Assert.assertEquals("en", html.getString("lang"));
        JSONObject head = html.getJSONObject("head");
        toJsonKeys(head, new String[]{"meta", "title", "link", "script"});
        JSONObject meta = head.getJSONObject("meta");
        toJsonKeys(meta, new String[]{"charset"});
        Assert.assertEquals("UTF-8", meta.getString("charset"));
        Assert.assertEquals("Scala XML", head.getString("title"));
        JSONObject link = head.getJSONObject("link");
        toJsonKeys(link, new String[]{"rel", "type", "href"});
        Assert.assertEquals("stylesheet", link.getString("rel"));
        Assert.assertEquals("text/css", link.getString("type"));
        Assert.assertEquals("style.css", link.getString("href"));
        JSONObject script = head.getJSONObject("script");
        toJsonKeys(script, new String[]{"type", "src"});
        Assert.assertEquals("text/javascript", script.getString("type"));
        Assert.assertEquals("script.js", script.getString("src"));
        JSONObject body = html.getJSONObject("body");
        toJsonKeys(body, new String[]{"form"});
        JSONObject form = body.getJSONObject("form");
        toJsonKeys(form, new String[]{"method", "input", "button"});
        Assert.assertEquals("post", form.getString("method"));
        JSONArray input = form.getJSONArray("input");
        Assert.assertEquals(2, input.size());
        toJsonKeys(input.getJSONObject(0), new String[]{"type", "name", "value"});
        Assert.assertEquals("text", input.getJSONObject(0).getString("type"));
        Assert.assertEquals("username", input.getJSONObject(0).getString("name"));
        Assert.assertEquals("root", input.getJSONObject(0).getString("value"));
        toJsonKeys(input.getJSONObject(1), new String[]{"type", "name"});
        Assert.assertEquals("password", input.getJSONObject(1).getString("type"));
        Assert.assertEquals("password", input.getJSONObject(1).getString("name"));
        JSONObject button = form.getJSONObject("button");
        toJsonKeys(button, new String[]{"id", "value"});
        Assert.assertEquals("sign-in", button.getString("id"));
        Assert.assertEquals("Sign In", button.getString("value"));
    }

    private void toJsonKeys(JSONObject json, String[] keys) {
        Assert.assertEquals(keys.length, json.keySet().size());
        int count = 0;
        for (Object key : json.keySet())
            for (String k : keys)
                if (k.equals(key))
                    count++;
        Assert.assertEquals(keys.length, count);
    }

    @Test
    public void toMap() {
        Assert.assertNull(xml.toMap(null, true));
        Assert.assertNull(xml.toMap("", false));
        toMapRoot("");
        toMap("");
        toMapRoot("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        toMap("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    }

    private void toMapRoot(String prefix) {
        Map<String, String> map = xml.toMap(prefix + string, true);
        Assert.assertEquals("en", map.get("html.lang"));
        Assert.assertEquals("UTF-8", map.get("html.head.meta.charset"));
        Assert.assertEquals("Scala XML", map.get("html.head.title"));
        Assert.assertEquals("stylesheet", map.get("html.head.link.rel"));
        Assert.assertEquals("text/css", map.get("html.head.link.type"));
        Assert.assertEquals("style.css", map.get("html.head.link.href"));
        Assert.assertEquals("text/javascript", map.get("html.head.script.type"));
        Assert.assertEquals("script.js", map.get("html.head.script.src"));
        Assert.assertEquals("post", map.get("html.body.form.method"));
        Assert.assertEquals("password", map.get("html.body.form.input.type"));
        Assert.assertEquals("password", map.get("html.body.form.input.name"));
        Assert.assertEquals("root", map.get("html.body.form.input.value"));
        Assert.assertEquals("Sign In", map.get("html.body.form.button"));
        Assert.assertEquals("sign-in", map.get("html.body.form.button.id"));
        Assert.assertEquals(14, map.size());
    }

    private void toMap(String prefix) {
        Map<String, String> map = xml.toMap(prefix + string, false);
        Assert.assertEquals("UTF-8", map.get("head.meta.charset"));
        Assert.assertEquals("Scala XML", map.get("head.title"));
        Assert.assertEquals("stylesheet", map.get("head.link.rel"));
        Assert.assertEquals("text/css", map.get("head.link.type"));
        Assert.assertEquals("style.css", map.get("head.link.href"));
        Assert.assertEquals("text/javascript", map.get("head.script.type"));
        Assert.assertEquals("script.js", map.get("head.script.src"));
        Assert.assertEquals("post", map.get("body.form.method"));
        Assert.assertEquals("password", map.get("body.form.input.type"));
        Assert.assertEquals("password", map.get("body.form.input.name"));
        Assert.assertEquals("root", map.get("body.form.input.value"));
        Assert.assertEquals("Sign In", map.get("body.form.button"));
        Assert.assertEquals("sign-in", map.get("body.form.button.id"));
        Assert.assertEquals(13, map.size());
    }
}
