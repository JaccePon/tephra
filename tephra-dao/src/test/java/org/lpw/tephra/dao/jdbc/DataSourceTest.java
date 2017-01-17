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
