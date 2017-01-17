# 管理JDBC连接
如果需要对JDBC连接进行管理，可以通过注入Connection对象，接口描述如下：
```java
package org.lpw.tephra.dao.jdbc;

/**
 * JDBC连接。
 *
 * @author lpw
 */
public interface Connection extends org.lpw.tephra.dao.Connection<java.sql.Connection> {
}
```
Tephra同时支持通过@javax.transaction.Transactional注解来强制控制事务，此时将自动调用beginTransaction管理事务。

事务控制测试：
```java
package org.lpw.tephra.dao.jdbc;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.dao.DaoUtil;
import org.lpw.tephra.dao.Mode;
import org.lpw.tephra.test.DaoTestSupport;

import javax.inject.Inject;

/**
 * @author lpw
 */
public class ConnectionTest extends DaoTestSupport {
    @Inject
    private Connection connection;

    @Test
    public void transactional() {
        BeanFactory.getBean(TransactionalImpl.class).get();
    }

    @Test
    public void beginTransaction() {
        connection.beginTransaction();
        java.sql.Connection connection1 = connection.get(null, Mode.Write);
        Assert.assertNotNull(connection1);
        java.sql.Connection connection2 = connection.get(null, Mode.Read);
        Assert.assertEquals(connection1.hashCode(), connection2.hashCode());

        DaoUtil.createDataSource("HasReadonlyBeginTransaction");
        java.sql.Connection connection3 = connection.get("HasReadonlyBeginTransaction", Mode.Write);
        Assert.assertNotNull(connection3);
        java.sql.Connection connection4 = connection.get("HasReadonlyBeginTransaction", Mode.Read);
        Assert.assertEquals(connection3.hashCode(), connection4.hashCode());
        connection.close();
    }

    @Test
    public void get() {
        java.sql.Connection connection1 = connection.get(null, Mode.Write);
        Assert.assertNotNull(connection1);
        java.sql.Connection connection2 = connection.get(null, Mode.Read);
        Assert.assertEquals(connection1.hashCode(), connection2.hashCode());

        DaoUtil.createDataSource("HasReadonlyGet");
        java.sql.Connection connection3 = connection.get("HasReadonlyGet", Mode.Write);
        Assert.assertNotNull(connection3);
        java.sql.Connection connection4 = connection.get("HasReadonlyGet", Mode.Read);
        Assert.assertNotEquals(connection3.hashCode(), connection4.hashCode());
        connection.close();
    }
}
```
TransactionalImpl：
```java
package org.lpw.tephra.dao.jdbc;

import org.junit.Assert;
import org.lpw.tephra.dao.DaoUtil;
import org.lpw.tephra.dao.Mode;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Repository("tephra.dao.jdbc.transactional.impl")
public class TransactionalImpl {
    @Inject
    private Connection connection;

    @javax.transaction.Transactional
    public void get() {
        java.sql.Connection connection1 = connection.get(null, Mode.Write);
        Assert.assertNotNull(connection1);
        java.sql.Connection connection2 = connection.get(null, Mode.Read);
        Assert.assertEquals(connection1.hashCode(), connection2.hashCode());

        DaoUtil.createDataSource("HasReadonlyTransactional");
        java.sql.Connection connection3 = connection.get("HasReadonlyTransactional", Mode.Write);
        Assert.assertNotNull(connection3);
        java.sql.Connection connection4 = connection.get("HasReadonlyTransactional", Mode.Read);
        Assert.assertEquals(connection3.hashCode(), connection4.hashCode());
    }
}
```
需要注意的是，通过beginTransaction()或@javax.transaction.Transactional强制控制事务时，将只使用可写连接，即不使用读写分离，可能会对数据库造成一定的压力。建议在对事务要求比较高的场景下使用。