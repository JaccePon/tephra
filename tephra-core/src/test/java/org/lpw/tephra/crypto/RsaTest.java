package org.lpw.tephra.crypto;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;
import org.lpw.tephra.util.Context;
import org.lpw.tephra.util.Generator;
import org.lpw.tephra.util.Thread;
import org.lpw.tephra.util.TimeUnit;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

/**
 * @author lpw
 */
public class RsaTest extends CoreTestSupport {
    @Inject
    private Context context;
    @Inject
    private Generator generator;
    @Inject
    private Thread thread;
    @Inject
    private Rsa rsa;

    @Test
    public void all() {
        Assert.assertEquals(2, Rsa.KeyType.values().length);
        Assert.assertEquals(Rsa.KeyType.Private, Rsa.KeyType.valueOf("Private"));
        Assert.assertEquals(Rsa.KeyType.Public, Rsa.KeyType.valueOf("Public"));

        rsa.generate(null, null, null);
        ByteArrayOutputStream publicKeyDer = new ByteArrayOutputStream();
        rsa.generate(publicKeyDer, null, null);
        ByteArrayOutputStream publicKeyX509 = new ByteArrayOutputStream();
        rsa.generate(publicKeyDer, publicKeyX509, null);
        File[] files = new File(context.getAbsolutePath("/WEB-INF/rsa")).listFiles();
        Assert.assertEquals(4, files.length);
        int count = 0;
        for (File file : files) {
            if (file.isFile()) {
                Assert.assertEquals("generate.sh", file.getName());
                count++;
            } else {
                Assert.assertEquals(32, file.getName().length());
                delete(file);
                count++;
            }
        }
        Assert.assertEquals(4, count);

        publicKeyX509 = new ByteArrayOutputStream();
        ByteArrayOutputStream privateKey = new ByteArrayOutputStream();
        rsa.generate(publicKeyDer, publicKeyX509, privateKey);
        thread.sleep(1, TimeUnit.Second);
        files = new File(context.getAbsolutePath("/WEB-INF/rsa")).listFiles();
        Assert.assertEquals(1, files.length);
        Assert.assertEquals("generate.sh", files[0].getName());

        Assert.assertNull(rsa.encrypt(null, null, null));
        Assert.assertNull(rsa.encrypt(Rsa.KeyType.Public, null, null));
        Assert.assertNull(rsa.encrypt(Rsa.KeyType.Public, publicKeyX509.toByteArray(), null));
        Assert.assertNull(rsa.encrypt(Rsa.KeyType.Public, publicKeyDer.toByteArray(), "text".getBytes()));
        Assert.assertNull(rsa.decrypt(null, null, null));
        Assert.assertNull(rsa.decrypt(Rsa.KeyType.Public, null, null));
        Assert.assertNull(rsa.decrypt(Rsa.KeyType.Public, publicKeyX509.toByteArray(), null));
        Assert.assertNull(rsa.decrypt(Rsa.KeyType.Public, publicKeyX509.toByteArray(), "text".getBytes()));
        Assert.assertNull(rsa.decrypt(Rsa.KeyType.Private, privateKey.toByteArray(), "text".getBytes()));

        byte[] text = generator.random(2048).getBytes();
        byte[] bytes = rsa.encrypt(Rsa.KeyType.Public, publicKeyX509.toByteArray(), text);
        Assert.assertArrayEquals(text, rsa.decrypt(Rsa.KeyType.Private, privateKey.toByteArray(), bytes));
        bytes = rsa.encrypt(Rsa.KeyType.Private, privateKey.toByteArray(), text);
        Assert.assertArrayEquals(text, rsa.decrypt(Rsa.KeyType.Public, publicKeyX509.toByteArray(), bytes));
    }

    private void delete(File file) {
        if (file.isDirectory())
            for (File f : file.listFiles())
                delete(f);

        file.delete();
    }
}
