# 使用JavaScript验证JSON参数
对于复杂的json参数，可以通过JavaScript脚本对参数的合法性进行验证。在控制器中：
```java
    @Execute(name = "js-validator", validates = {
            @Validate(validator = ScriptValidator.NAME, parameter = "parameter", string = {"validator1", "validator2"})
    })
```
其中parameter为json参数的name，string的值为相应JavaScript验证器绑定的名称。

在非控制器中，可以通过注入ScriptService实现，并调用validate方法进行验证：
```java
package org.lpw.tephra.script;

import net.sf.json.JSONObject;

/**
 * @author lpw
 */
public interface ScriptService {
    /**
     * 脚本方法名是否存在验证器。
     */
    String VALIDATOR_EXISTS_METHOD = "tephra.script.validator.exists-method";

    /**
     * 验证JSON格式的数据是否有效。
     *
     * @param names     JavaScript验证器名称。
     * @param parameter JSON格式的参数值。
     * @return 验证结果。
     */
    JSONObject validate(String[] names, String parameter);
}
```
## 定义JavaScript验证器
1、在${tephra.script.path}目录下编写如下JavaScript（va.js）：
```javascript
tephra.validator("validator1", function (json) {
    if (!json.name)
        return {code: 1234, failure: "你错了"};
 
    return {code: 0};
});
 
tephra.validator("validator2", function (json) {
    if (json.name.length < 10)
        return {code: 2345, failure: tephra.message.get("hi error")};
 
    return {code: 0};
});
```
该文件绑定了两个验证器["hello","hi"]。

2、将验证器文件添加到name列表：
```txt
/tephra.js
/validate.js
/va.js
```
3、刷新脚本引擎，验证器便可立即生效。

## 脚本调试工具
启动应用后，在Chrome中访问http://localhost:8080/tephra/script/debug，可以对脚本验证器进行调试，同时借助于Chrome的断点调试功能，可以对脚本验证器进行断点调试。