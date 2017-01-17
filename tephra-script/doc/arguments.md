# 在Java和JavaScript之间传递参数
在Java和JavaScript之间传递参数可以使用Arguments：
```java
package org.lpw.tephra.script;

import java.util.Map;

/**
 * 脚本请求参数集。
 *
 * @author lpw
 */
public interface Arguments {
    /**
     * 获取请求参数。
     *
     * @param name 参数名。
     * @return 参数值；如果不存在则返回null。
     */
    Object get(String name);

    /**
     * 设置参数。
     *
     * @param name  参数名。
     * @param value 参数值。
     */
    void set(String name, Object value);

    /**
     * 获取所有参数。
     *
     * @return 参数集；如果不存在则返回空集。
     */
    Map<String, Object> all();
}
```
1、JavaScript
```javascript
function arguments_execute() {
    var arg = tephra.arguments.get("arg");
    tephra.arguments.set("arg", "arg from javascript");

    return arg;
};

function arguments_all() {
    var array = tephra.arguments.all();
    var sum = 0;
    for (var i = 0; i < 10; i++)
        sum += array["arg" + i];

    return sum;
};
```
2、Java
```java
package org.lpw.tephra.script;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;

import javax.inject.Inject;

/**
 * @author lpw
 */
public class ArgumentsTest extends CoreTestSupport {
    @Inject
    private Engine engine;
    @Inject
    private Arguments arguments;

    @Test
    public void execute() throws Exception {
        arguments.set("arg", "arg from java");
        Assert.assertEquals("arg from java", engine.execute("arguments_execute"));
        Assert.assertEquals("arg from javascript", arguments.get("arg"));
    }

    @Test
    public void all() throws Exception {
        arguments.all().clear();
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            arguments.set("arg" + i, i);
            sum += i;
        }
        Assert.assertEquals(sum * 1.0D, engine.execute("arguments_all"));
    }
}
```