# 使用拦截器
Ctrl提供了一个拦截器接口，只需实现该接口便可在服务被执行前后对请求进行拦截。
```java
package org.lpw.tephra.ctrl;

/**
 * 控制层拦截器。
 *
 * @author lpw
 */
public interface Interceptor {
    /**
     * 获得拦截器的执行顺序。执行顺序小的优先执行。
     *
     * @return 执行顺序。
     */
    int getSort();

    /**
     * 执行拦截器调用。
     *
     * @param invocation 执行调用。
     * @return 执行结果。
     * @throws Exception 运行期异常。
     */
    Object execute(Invocation invocation) throws Exception;
}
```
如果允许继续执行服务，则调用invocation.invoke()，如：
```java
package org.lpw.spark.scrum.last;
 
import org.lpw.tephra.ctrl.Interceptor;
import org.lpw.tephra.ctrl.Invocation;
import org.lpw.tephra.ctrl.context.Request;
import org.springframework.stereotype.Controller;
 
import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller("spark.last.interceptor")
public class LastInterceptorImpl implements Interceptor {
    @Inject
    private Request request;
    @Inject
    private LastService lastService;
 
    @Override
    public int getSort() {
        return 11;
    }
 
    @Override
    public Object execute(Invocation invocation) throws Exception {
        lastService.setUri(request.getUri());
 
        return invocation.invoke();
    }
}
```
