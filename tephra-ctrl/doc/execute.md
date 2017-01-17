# 使用Execute注解定义服务
Tephra使用Execute注解定义服务，并且允许在类上定义URI前缀。
```java
package org.lpw.tephra.ctrl.execute;

import org.lpw.tephra.ctrl.validate.Validate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 执行器。用于标注执行服务。
 *
 * @author lpw
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Execute {
    /**
     * 服务名称。支持使用正则表达式；多个名称间以逗号分隔。
     *
     * @return 服务名称。
     */
    String name();

    /**
     * 验证规则集。
     *
     * @return 验证规则集。
     */
    Validate[] validates() default {};

    /**
     * 输出类型。
     *
     * @return 输出类型。
     */
    String type() default "";

    /**
     * 输出模版。
     *
     * @return 输出模版。
     */
    String template() default "";

    /**
     * 错误编码。
     *
     * @return 错误编码。
     */
    String code() default "";
}
```
如下定义了两个服务：
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
@Execute(name = "/tephra/")
public class HelloCtrl {
    @Inject
    private Request request;
 
    @Execute(name = "hello")
    public Object hello() {
        return "hello " + request.get("name");
    }
 
    @Execute(name = "hi", type = "freemarker", template = "hi")
    public Object hi() {
        Map<String, String> map = new HashMap<>();
        map.put("name", request.get("name"));
 
        return map;
    }
}
```
hi.ftl：
```freemarker
hi ${data.name} !
```
执行结果：
```shell
$ curl http://localhost:8080/tephra/hello?name=tephra
{"code":0,"data":"hello tephra"}
 
$ curl http://localhost:8080/tephra/hi?name=tephra
hi tephra !
```
