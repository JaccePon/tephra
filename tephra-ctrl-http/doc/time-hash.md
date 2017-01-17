# 时间哈希验证
对于一些需要验证请求来源的服务，Ctrl－Http提供了一个时间哈希验证；由请求方在发起请求时生成一个时间哈希值，并添加到HTTP头中，服务端在处理请求前验证时间哈希值是否合法，对于非合法访问将直接返回404。由于该算法消耗的cpu时间极短（每百万次耗时150毫秒左右），因此对性能的影响几乎可以忽略不计。

1、服务端开启时间哈希验证（core.tephra.config.properties）：
```properties
## 设置时间哈希有效时长，单位：秒。
## 如果设置为0或负数则表示不启用时间哈希验证（验证时总返回true）。
#tephra.util.time-hash.range = 0
```
2、客户端在提交请求时，在HTTP头信息中添加time-hash参数，参数值为新生成的时间哈希值。

3、获取服务端时间戳（用于计算时间偏移量）：
```shell
curl http://${server}/tephra/ctrl/status
{"code":0,"data":{"concurrent":1,"timestamp":1466041778868}}
```
其中${server}为服务器地址，timestamp的值即为服务端当前时间戳。

## Android客户端
待续。

## iOS客户端
待续。

## Node.js
待续。

## 忽略校验
通过实现IgnoreTimeHash接口，可以指定当前请求不进行时间哈希校验：
```java
package org.lpw.tephra.ctrl.http;

/**
 * 忽略时间哈希校验。
 *
 * @author lpw
 */
public interface IgnoreTimeHash {
    /**
     * 验证当前请求是否忽略时间哈希校验。
     *
     * @return true表示忽略，即不校验时间哈希。
     */
    boolean ignore();
}
```
在ignore实现中，可以通过注入Request获取到当前请求的URI或参数，来判断是否忽略。

如果项目中未实现此借口，则默认进行校验。
