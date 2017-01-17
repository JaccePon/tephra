# 使用Mock测试控制器

1、引入tephra-test依赖，如：
```xml
        <dependency>
            <groupId>org.lpw.tephra</groupId>
            <artifactId>tephra-test</artifactId>
            <version>1.0.0-RELEASE</version>
            <scope>test</scope>
        </dependency>
```
2、假设控制器代码如下：
```java
package org.lpw.hellotephra;
 
import org.lpw.tephra.ctrl.context.Request;
import org.lpw.tephra.ctrl.execute.Execute;
import org.springframework.stereotype.Controller;
 
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
 
/**
 * @author lpw
 */
@Controller("hellotephra.ctrl")
public class HelloCtrl {
    @Inject
    private Request request;
 
    @Execute(name = "/hello")
    public Object hello() {
        return "hello " + request.get("name");
    }
 
    @Execute(name = "/hi", type = "freemarker", template = "hi")
    public Object hi() {
        Map<String, String> map = new HashMap<>();
        map.put("name", request.get("name"));
 
        return map;
    }
}
```
hi.ftl代码如下：
```freemarker
hi ${data.name} !
```
> 模板文件需保存到classpath下${tephra.freemarker.root}设置路径中。

3、则HelloCtrl测试代码参考如下：
```java
package org.lpw.hellotephra;
 
import org.junit.Test;
import org.lpw.tephra.test.TephraTestSupport;
import org.lpw.tephra.test.MockHelper;
import org.lpw.tephra.util.Context;
import org.springframework.beans.factory.annotation.Inject;
 
import java.io.File;
import java.io.IOException;
 
import static org.junit.Assert.assertEquals;
 
/**
 * @author lpw
 */
public class HelloCtrlTest extends TephraTestSupport {
    @Inject
    private Context context;
    @Inject
    private MockHelper mockHelper;
 
    @Test
    public void hello() {
        mockHelper.reset();
        mockHelper.getRequest().addParameter("name", "tephra");
        mockHelper.mock("/hello");
        assertEquals("{\"code\":0,\"data\":\"hello tephra\"}", mockHelper.getResponse().getOutputStream().toString());
    }
 
    @Test
    public void hi() {
        mockHelper.reset();
        mockHelper.getRequest().addParameter("name", "tephra");
        mockHelper.mock("/hi");
        assertEquals("hi tephra !", mockHelper.getResponse().getOutputStream().toString());
    }
}
```
4、输出结果：
```log
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
```