package org.lpw.tephra.crypto;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Io;
import org.lpw.tephra.util.Thread;
import org.lpw.tephra.util.TimeUnit;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
public class SignTest extends CoreTestSupport {
    @Inject
    private Converter converter;
    @Inject
    private Context context;
    @Inject
    private Io io;
    @Inject
    private Thread thread;
    @Inject
    private Digest digest;
    @Inject
    private Sign sign;

    @Test
    public void put() {
        String path = context.getAbsolutePath("/WEB-INF/sign");
        byte[] bytes = io.read(path);
        io.write(path, "test key\n=new default key\nsign=new sign key".getBytes());
        thread.sleep(2, TimeUnit.Second);
        sign.put(null, "name");

        Map<String, String> map = new HashMap<>();
        sign.put(map, "name");
        Assert.assertEquals(2, map.size());
        Assert.assertTrue(System.currentTimeMillis() - converter.toLong(map.get("sign-time")) < 2L * 1000);
        Assert.assertEquals(digest.md5("sign-time=" + map.get("sign-time") + "&new default key"), map.get("sign"));

        map.clear();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            map.put("key " + i, "value " + i);
            sb.append("key ").append(i).append('=').append("value ").append(i).append('&');
        }
        sign.put(map, "name");
        Assert.assertEquals(7, map.size());
        for (int i = 0; i < 5; i++)
            Assert.assertEquals("value " + i, map.get("key " + i));
        Assert.assertTrue(System.currentTimeMillis() - converter.toLong(map.get("sign-time")) < 2L * 1000);
        Assert.assertEquals(digest.md5(sb.append("sign-time=").append(map.get("sign-time")).append("&new default key").toString()), map.get("sign"));

        map.clear();
        sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            map.put("key " + i, "value " + i);
            sb.append("key ").append(i).append('=').append("value ").append(i).append('&');
        }
        sign.put(map, "sign");
        Assert.assertEquals(7, map.size());
        for (int i = 0; i < 5; i++)
            Assert.assertEquals("value " + i, map.get("key " + i));
        Assert.assertTrue(System.currentTimeMillis() - converter.toLong(map.get("sign-time")) < 2L * 1000);
        Assert.assertEquals(digest.md5(sb.append("sign-time=").append(map.get("sign-time")).append("&new sign key").toString()), map.get("sign"));

        map.clear();
        sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            map.put("key " + i, "value " + i);
            sb.append("key ").append(i).append('=').append("value ").append(i).append('&');
        }
        sign.put(map, null);
        Assert.assertEquals(7, map.size());
        for (int i = 0; i < 5; i++)
            Assert.assertEquals("value " + i, map.get("key " + i));
        Assert.assertTrue(System.currentTimeMillis() - converter.toLong(map.get("sign-time")) < 2L * 1000);
        Assert.assertEquals(digest.md5(sb.append("sign-time=").append(map.get("sign-time")).append("&new default key").toString()), map.get("sign"));

        map.clear();
        sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            map.put("key " + i, "value " + i);
            sb.append("key ").append(i).append('=').append("value ").append(i).append('&');
        }
        sign.put(map, "");
        Assert.assertEquals(7, map.size());
        for (int i = 0; i < 5; i++)
            Assert.assertEquals("value " + i, map.get("key " + i));
        Assert.assertTrue(System.currentTimeMillis() - converter.toLong(map.get("sign-time")) < 2L * 1000);
        Assert.assertEquals(digest.md5(sb.append("sign-time=").append(map.get("sign-time")).append("&new default key").toString()), map.get("sign"));

        io.write(path, bytes);
        thread.sleep(2, TimeUnit.Second);

        map.clear();
        sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            map.put("key " + i, "value " + i);
            sb.append("key ").append(i).append('=').append("value ").append(i).append('&');
        }
        sign.put(map, "");
        Assert.assertEquals(7, map.size());
        for (int i = 0; i < 5; i++)
            Assert.assertEquals("value " + i, map.get("key " + i));
        Assert.assertTrue(System.currentTimeMillis() - converter.toLong(map.get("sign-time")) < 2L * 1000);
        Assert.assertEquals(digest.md5(sb.append("sign-time=").append(map.get("sign-time")).append("&default key").toString()), map.get("sign"));

        map.clear();
        sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            map.put("key " + i, "value " + i);
            sb.append("key ").append(i).append('=').append("value ").append(i).append('&');
        }
        sign.put(map, "key");
        Assert.assertEquals(7, map.size());
        for (int i = 0; i < 5; i++)
            Assert.assertEquals("value " + i, map.get("key " + i));
        Assert.assertTrue(System.currentTimeMillis() - converter.toLong(map.get("sign-time")) < 2L * 1000);
        Assert.assertEquals(digest.md5(sb.append("sign-time=").append(map.get("sign-time")).append("&sign key").toString()), map.get("sign"));
    }

    @Test
    public void verify() {
        thread.sleep(2, TimeUnit.Second);
        Assert.assertFalse(sign.verify(null, null));

        Map<String, String> map = new HashMap<>();
        Assert.assertFalse(sign.verify(map, null));

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            map.put("key " + i, "value " + i);
            sb.append("key ").append(i).append('=').append("value ").append(i).append('&');
        }
        Assert.assertFalse(sign.verify(map, null));
        map.put("sign", "sign");
        Assert.assertFalse(sign.verify(map, null));
        map.put("sign-time", "123456");
        Assert.assertFalse(sign.verify(map, null));
        map.put("sign-time", converter.toString(System.currentTimeMillis() - 12 * TimeUnit.Second.getTime(), "0"));
        Assert.assertFalse(sign.verify(map, null));
        map.put("sign-time", converter.toString(System.currentTimeMillis() - 5 * TimeUnit.Second.getTime(), "0"));
        Assert.assertFalse(sign.verify(map, null));
        map.put("sign", digest.md5(sb.append("sign-time=").toString() + map.get("sign-time") + "&default key"));
        Assert.assertTrue(sign.verify(map, null));
        map.put("sign-time", converter.toString(System.currentTimeMillis(), "0"));
        map.put("sign", digest.md5(sb.toString() + map.get("sign-time") + "&sign key"));
        Assert.assertTrue(sign.verify(map, "key"));
        map.put("sign", digest.md5(sb.toString() + map.get("sign-time") + "&default key"));
        Assert.assertTrue(sign.verify(map, "sign"));
    }
}
