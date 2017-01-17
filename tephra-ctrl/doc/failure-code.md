# 验证错误编码管理
通过定义@Execute的code属性，可以统一设置请求的错误编码前缀，如以下定义：
```java
package org.lpw.tephra.ctrl;
 
import org.lpw.tephra.ctrl.execute.Execute;
import org.lpw.tephra.ctrl.validate.Validate;
import org.lpw.tephra.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;
 
/**
 * @author lpw
 */
@Controller("tephra.ctrl.failure-code")
@Execute(name = "/tephra/ctrl/failure-code/", code = "10")
public class FailureCodeCtrl {
    @Execute(name = "execute", code = "01", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "name", failureCode = 1, failureArgKeys = {"tephra.ctrl.failure-code.name"})
    })
    public Object execute() {
        return "";
    }
}
```
测试与使用：
```java
package org.lpw.tephra.ctrl;

import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.ctrl.mock.MockHelper;
import org.lpw.tephra.test.DaoTestSupport;

import javax.inject.Inject;

/**
 * @author lpw
 */
public class FailureCodeTest extends DaoTestSupport {
    @Inject
    private MockHelper mockHelper;
    @Inject
    private FailureCode failureCode;

    @Test
    public void get() {
        mockHelper.reset();
        mockHelper.mock("/tephra/ctrl/failure-code/execute");
        JSONObject json = mockHelper.getResponse().asJson();
        Assert.assertEquals(100101, json.getInt("code"));
        for (int i = 0; i < 10; i++)
            Assert.assertEquals(100100 + i, failureCode.get(i));
    }
}
```
如果不同的URI地址设置了相同的编码前缀，则启动时将发出如下警告：
```log
WARN  2016-05-16 16:19:45 org.lpw.tephra.ctrl.execute.ExecutorHelperImpl 重复的错误编码[9901]设置！
```