# 测试普通Java Bean

## 使用SpringJUnit4ClassRunner

普通Java Bean可以直接使用SpringJUnit4ClassRunner运行，首先需引入以下依赖
```xml
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>LATEST</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>LATEST</version>
            <scope>test</scope>
        </dependency>
```
然后在测试类上添加RunWith、ContextConfiguration注解，如：
```java
package org.lpw.tephra.crypto;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

/**
 * @author lpw
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:**/spring.xml"})
public class DigestTest {
    @Inject
    private Digest digest;

    @Test
    public void md5() {
        String string = null;
        Assert.assertNull(digest.md5(string));
        byte[] bytes = null;
        Assert.assertNull(digest.md5(bytes));
        Assert.assertEquals("c10f77963a2b21079156a0e5c5a4bb3c", digest.md5("digest"));
        Assert.assertEquals("c10f77963a2b21079156a0e5c5a4bb3c", digest.md5("digest".getBytes()));
    }

    @Test
    public void sha1() {
        String string = null;
        Assert.assertNull(digest.sha1(string));
        byte[] bytes = null;
        Assert.assertNull(digest.sha1(bytes));
        Assert.assertEquals("2923f6fa36614586ea09b4424b438915cc1b9b67", digest.sha1("digest"));
        Assert.assertEquals("2923f6fa36614586ea09b4424b438915cc1b9b67", digest.sha1("digest".getBytes()));
    }
}
```
> 使用ContextConfiguration需要在classpath中添加spring.xml文件。

## 继承TephraTestSupport

首先需引入以下依赖：
```xml
<dependency>
    <groupId>org.lpw.tephra</groupId>
    <artifactId>tephra-test</artifactId>
    <version>1.0.0-RELEASE</version>
</dependency>
```
然后测试类继承TephraTestSupport类，如：
```java
package org.lpw.tephra.crypto;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;

import javax.inject.Inject;
import java.lang.reflect.Method;

/**
 * @author lpw
 */
public class DigestTest extends CoreTestSupport {
    @Inject
    private Digest digest;

    @Test
    public void md5() {
        Assert.assertNull(digest.md5((String) null));
        Assert.assertNull(digest.md5((byte[]) null));
        Assert.assertEquals("c10f77963a2b21079156a0e5c5a4bb3c", digest.md5("digest"));
        Assert.assertEquals("c10f77963a2b21079156a0e5c5a4bb3c", digest.md5("digest".getBytes()));
    }

    @Test
    public void sha1() {
        Assert.assertNull(digest.sha1((String) null));
        Assert.assertNull(digest.sha1((byte[]) null));
        Assert.assertEquals("2923f6fa36614586ea09b4424b438915cc1b9b67", digest.sha1("digest"));
        Assert.assertEquals("2923f6fa36614586ea09b4424b438915cc1b9b67", digest.sha1("digest".getBytes()));
    }

    @Test
    public void digest() throws Exception {
        Method method = DigestImpl.class.getDeclaredMethod("digest", String.class, byte[].class);
        method.setAccessible(true);
        Assert.assertNull(method.invoke(digest, "algorithm", "digest".getBytes()));
    }
}
```
> 使用TephraTestSupport不需要在classpath中添加spring.xml文件。

## Mock SQL脚本
当使用TephraTestSupport时，会自动在每次测试前，执行以下SQL脚本，可用来创建测试表结构及测试数据。
- /src/main/sql/create.sql
- /src/test/sql/mock.sql
