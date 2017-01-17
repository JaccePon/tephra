# 执行JavaScript
1、在script.tephra.config.properties设置：
```properties
## 设置脚本根路径。
#tephra.script.path = /WEB-INF/script
```
2、在${tephra.script.path}目录下新建name文件，并引入tephra.js文件，及其他自定义JavaScript文件。
```txt
/tephra.js
/validate.js
```
3、如果需要在JavaScript中引用Java Bean，则可以在tephra中添加引用：
```javascript
function tephra() {
};

tephra.BeanFactory = Java.type("org.lpw.tephra.bean.BeanFactory");
tephra.cache = tephra.BeanFactory.getBean("tephra.cache");
tephra.message = tephra.BeanFactory.getBean("tephra.util.message");
tephra.logger = tephra.BeanFactory.getBean("tephra.util.logger");
tephra.sql = tephra.BeanFactory.getBean("tephra.dao.sql");
tephra.ctrl = {
    header: tephra.BeanFactory.getBean("tephra.ctrl.context.header"),
    session: tephra.BeanFactory.getBean("tephra.ctrl.context.session"),
    request: tephra.BeanFactory.getBean("tephra.ctrl.context.request")
};
tephra.args = tephra.BeanFactory.getBean("tephra.script.arguments");

tephra.ready = function (func) {
    tephra.ready.functions[tephra.ready.functions.length] = func;
};

tephra.ready.functions = [];

tephra.ready.execute = function () {
    if (tephra.ready.functions.length == 0)
        return;

    for (var i = 0; i < tephra.ready.functions.length; i++) {
        if (!tephra.ready.functions[i])
            continue;

        if (typeof (tephra.ready.functions[i]) == "function")
            tephra.ready.functions[i]();
        else if (typeof (tephra.ready.functions[i]) == "string")
            eval(tephra.ready.functions[i]);

        tephra.ready.functions[i] = null;
    }
};

tephra.existsMethod = function () {
    try {
        var method = tephra.arguments.get("method");
        if (!method)
            method = tephra.ctrl.request.get("method");

        return typeof (eval(method)) == "function";
    } catch (e) {
        return false;
    }
};
```
4、要执行的JavaScript：
```javascript
function engine_execute() {
    return "message from javascript";
};
```
5、执行：
```java
package org.lpw.tephra.script;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;

import javax.inject.Inject;
import java.io.IOException;

/**
 * @author lpw
 */
public class EngineTest extends CoreTestSupport {
    @Inject
    private Engine engine;

    @Test
    public void execute() throws IOException {
        Assert.assertEquals("message from javascript", engine.execute("engine_execute"));
    }
}
```