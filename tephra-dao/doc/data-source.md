# 管理数据源
Dao模块允许在运行时对数据源进行动态创建与获取，DataSource接口描述如下：
```java
package org.lpw.tephra.dao.jdbc;

import org.lpw.tephra.dao.ConnectionFactory;
import org.lpw.tephra.dao.dialect.Dialect;

import java.util.List;
import java.util.Map;

/**
 * @author lpw
 */
public interface DataSource extends ConnectionFactory<javax.sql.DataSource> {
    /**
     * 获取只读数据源集。
     *
     * @param name 数据源引用名称。
     * @return 只读数据源集；如果不存在则返回null。
     */
    List<javax.sql.DataSource> listReadonly(String name);

    /**
     * 验证是否包含只读数据源。
     *
     * @param name 数据源引用名称。
     * @return 如果包含只读数据源则返回true；否则返回false。
     */
    boolean hasReadonly(String name);

    /**
     * 获取数据库方言。
     *
     * @return 数据库方言。
     */
    Map<String, Dialect> getDialects();
}
```
示例与单元测试：
```java
package org.lpw.tephra.dao.jdbc;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.dao.DaoUtil;
import org.lpw.tephra.dao.dialect.Dialect;
import org.lpw.tephra.test.DaoTestSupport;
import org.lpw.tephra.util.Generator;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * @author lpw
 */
public class DataSourceTest extends DaoTestSupport {
    @Inject
    private Generator generator;
    @Inject
    private DataSource dataSource;

    @Test
    public void config() {
        javax.sql.DataSource ds = dataSource.getWriteable("");
        Assert.assertNotNull(ds);
        Assert.assertEquals(ds.hashCode(), dataSource.getReadonly("").hashCode());
        Assert.assertNull(dataSource.listReadonly(""));

        Map<String, Dialect> map = dataSource.getDialects();
        Assert.assertEquals("mysql", map.get("").getName());
    }

    @Test
    public void create() {
        String name = generator.chars(8);
        Assert.assertNull(dataSource.getWriteable(name));

        DaoUtil.createDataSource(name);
        javax.sql.DataSource writeable = dataSource.getWriteable(name);
        Assert.assertNotNull(writeable);
        javax.sql.DataSource readonly = dataSource.getReadonly(name);
        Assert.assertNotNull(readonly);
        Assert.assertNotEquals(writeable.hashCode(), readonly.hashCode());
        List<javax.sql.DataSource> list = dataSource.listReadonly(name);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(readonly.hashCode(), list.get(0).hashCode());

        Map<String, Dialect> map = dataSource.getDialects();
        Assert.assertEquals("mysql", map.get(name).getName());
    }
}
```