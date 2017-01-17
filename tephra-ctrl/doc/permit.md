# 权限验证
控制器在执行拦截器及服务之前，如果检测到有Permit实现，则会优先验证是否允许执行此服务，如果不允许则返回未获得访问权限的相关信息。
```java
package org.lpw.tephra.ctrl;

/**
 * 权限验证器。
 *
 * @author lpw
 */
public interface Permit {
    /**
     * 验证权限。
     *
     * @return 如果允许访问则返回true；否则返回false。
     */
    boolean allow();
}
```
一个示例：
```java
package org.lpw.spark.user;
 
import org.lpw.tephra.ctrl.Permit;
import org.lpw.tephra.ctrl.context.Request;
import org.springframework.stereotype.Controller;
 
import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller("spark.user.permit")
public class PermitImpl implements Permit {
    @Inject
    private Request request;
    @Inject
    private UserService userService;
 
    @Override
    public boolean allow() {
        String uri = request.getUri();
 
        return uri.length() <= 1 || uri.startsWith("/spark/user/sign-") || uri.endsWith(".htm") || userService.get() != null;
    }
}
```
需要注意的是，在一个项目中只允许存在一个Permit实现。