package org.lpw.tephra.crypto;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;

import javax.inject.Inject;

/**
 * @author lpw
 */
public class DesTest extends CoreTestSupport {
    @Inject
    private Des des;

    @Test
    public void crypto() {
        Assert.assertNull(des.encrypt(null, null));
        byte[] key = "12345678".getBytes();
        Assert.assertNull(des.encrypt(key, null));
        byte[] text = "text".getBytes();
        Assert.assertNull(des.encrypt(null, text));
        Assert.assertNull(des.decrypt(key, text));
        Assert.assertArrayEquals(text, des.decrypt(key, des.encrypt(key, text)));
        key = "123456".getBytes();
        Assert.assertNull(des.encrypt(key, text));
    }
}
