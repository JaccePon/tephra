package org.lpw.tephra.crypto;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;

import javax.inject.Inject;

/**
 * @author lpw
 */
public class XorShiftTest extends CoreTestSupport {
    @Inject
    private XorShift xorShift;

    @Test
    public void crypto() {
        Assert.assertNull(xorShift.encrypt(null, null));
        Assert.assertNull(xorShift.decrypt(null, null));
        byte[] key = "12345678".getBytes();
        Assert.assertNull(xorShift.encrypt(key, null));
        Assert.assertNull(xorShift.decrypt(key, null));
        byte[] text = "text".getBytes();
        Assert.assertNull(xorShift.encrypt(null, text));
        Assert.assertNull(xorShift.decrypt(null, text));
        Assert.assertArrayEquals(text, xorShift.decrypt(key, xorShift.encrypt(key, text)));
        text = "texttexttexttexttexttext".getBytes();
        Assert.assertArrayEquals(text, xorShift.decrypt(key, xorShift.encrypt(key, text)));
    }
}
