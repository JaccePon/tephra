# 连接工厂与连接
Dao模块定义了数据连接工厂规范与数据连接规范，用于定义通用的数据连接接口。

ConnectionFactory：
```java
package org.lpw.tephra.dao;

import net.sf.json.JSONObject;

/**
 * DAO连接工厂。
 * 定义DAO连接工厂接口。
 *
 * @author lpw
 */
public interface ConnectionFactory<T> {
    /**
     * 获取可写数据连接。
     *
     * @param name 数据源引用名称。
     * @return 可写数据连接；如果不存在则返回null。
     */
    T getWriteable(String name);

    /**
     * 获取只读数据连接。
     *
     * @param name 数据源引用名称。
     * @return 只读数据连接；如果不存在则返回null。
     */
    T getReadonly(String name);

    /**
     * 创建数据连接。
     *
     * @param config 数据连接配置。
     */
    void create(JSONObject config);
}
```
Connection：
```java
package org.lpw.tephra.dao;

/**
 * DAO连接。
 * 定义DAO连接接口。
 *
 * @author lpw
 */
public interface Connection<T> extends Commitable {
    /**
     * 开始事务控制。
     * 开始后当前线程通过get方法请求的Connection均为同一个，并且为可读写连接实例。
     * 也可以通过在方法上添加@javax.transaction.Transactional注解来开启。
     * 事务会在rollback或close方法被调用时，自动提交并结束。
     */
    void beginTransaction();

    /**
     * 获取一个数据连接。返回线程安全的数据连接，即每个线程使用独立的数据连接。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param mode       数据操作方式 ；为null则获取可读写数据源。
     * @return 数据连接；如果获取失败则返回null。
     */
    T get(String dataSource, Mode mode);
}
```
Mode：
```java
package org.lpw.tephra.dao;

/**
 * 数据库操作方式。
 *
 * @author lpw
 */
public enum Mode {
    /**
     * 读。
     */
    Read,
    /**
     * 写。
     */
    Write
}
```