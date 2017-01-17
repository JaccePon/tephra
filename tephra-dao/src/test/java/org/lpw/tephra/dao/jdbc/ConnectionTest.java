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
