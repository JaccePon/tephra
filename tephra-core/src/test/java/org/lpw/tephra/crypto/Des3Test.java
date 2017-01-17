package org.lpw.tephra.crypto;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;

import javax.inject.Inject;

/**
 * @author lpw
 */
public class Des3Test extends CoreTestSupport {
    @Inject
    private Des3 des3;

    @Test
    public void crypto() {
        Assert.assertNull(des3.encrypt(null, null));
        byte[] key = "123456781234567812345678".getBytes();
        Assert.assertNull(des3.encrypt(key, null));
        byte[] text = "text".getBytes();
        Assert.assertNull(des3.encrypt(null, text));
        Assert.assertNull(des3.decrypt(key, text));
        Assert.assertArrayEquals(text, des3.decrypt(key, des3.encrypt(key, text)));
        key = "12345678".getBytes();
        Assert.assertNull(des3.encrypt(key, text));
    }
}
