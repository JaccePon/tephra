# 快速开始

1、为Java模块添加tephra依赖：
```xml
    <dependency>
        <groupId>org.lpw.tephra</groupId>
        <artifactId>tephra-ctrl</artifactId>
        <version>1.0.0-RELEASE</version>
    </dependency>
```
2、为web模块添加tephra依赖：
```xml
    <dependency>
        <groupId>org.lpw.tephra</groupId>
        <artifactId>tephra-web</artifactId>
        <version>1.0.0-RELEASE</version>
        <type>war</type>
    </dependency>
```
3、删除web模块中的web.xml，或者从tephra-web中复制web.xml到web模块中。

4、添加控制器：
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
5、执行maven指令并启动服务：
```maven
clean install -Dmaven.test.skip=true
```
6、测试结果：
```sh
curl http://localhost:8080/hello?name=Tephra
{"code":0,"data":"hello Tephra"}
 
curl http://localhost:8080/hi?name=Tephra
hi Tephra !
```
7、日志显示如下：
```log
DEBUG 2015-12-16 13:55:15 org.lpw.tephra.ctrl.DispatcherImpl 开始处理请求[/hello]。
DEBUG 2015-12-16 13:55:15 org.lpw.tephra.ctrl.DispatcherImpl 处理请求[/hello]完成，耗时[1]毫秒。
DEBUG 2015-12-16 13:55:29 org.lpw.tephra.ctrl.DispatcherImpl 开始处理请求[/hi]。
DEBUG 2015-12-16 13:55:30 org.lpw.tephra.ctrl.DispatcherImpl 处理请求[/hi]完成，耗时[126]毫秒。
```