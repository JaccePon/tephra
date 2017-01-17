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
