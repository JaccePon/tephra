# 请求参数签名
通过对请求参数进行签名与验证，可以有效防止参数在传输过程中参数被篡改，并且能拒绝非法的API请求。
1、设置密钥 & 有效时长：
```property
## 设置签名密钥文件路径。
#tephra.crypto.sign.path = /WEB-INF/sign
## 设置签名有效时长，单位：毫秒。
#tephra.crypto.sign.time = 10000
```
sign文件：
```text
## 设置签名密钥。
## 每行一个密钥，使用【密钥名=密钥】的形式。
## 如果密钥名为空则为默认密钥，默认密钥在未提供密钥名时使用。
=
```
> sign文件在被修改后会自动重载，无需重启服务。

2、在请求端，可通过request添加签名：
```java
package org.lpw.tephra.ctrl.context;

import java.util.Map;

/**
 * 请求。
 *
 * @author lpw
 */
public interface Request {
    /**
     * 添加请求消息摘要验证串。
     *
     * @param map 参数集。
     */
    void putSign(Map<String, String> map);
}
```
> 将往map参数添加sign-time、sign两个参数，分别为签名时间和签名摘要。

3、在服务端，可通过Validators.SIGN验证器进行验证：
```java
    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
```
4、签名算法：

1. 将所有参数（包含sign-time，但不包含sign）按名称升序排列；
1. 将参数名与参数值用等号“=”连接；
1. 所有参数以“&”符号连接；
1. 将连接后的参数加上“&”+密钥；
1. 对数据进行MD5消息摘要计算，得出sign值。

如参数为：username=tephra，password=hello，captcha=123456，当前时间戳为：1483067727747（精确到毫秒），密钥为：secret key，则消息签名值为：MD5(captcha=123456&password=hello&sign-time=1483067727747&username=tephra&secret key)。
