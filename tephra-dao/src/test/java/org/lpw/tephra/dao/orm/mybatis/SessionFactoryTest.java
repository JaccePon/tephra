package org.lpw.tephra.dao.orm.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.dao.DaoUtil;
import org.lpw.tephra.test.DaoTestSupport;

import javax.inject.Inject;

/**
 * @author lpw
 */
public class SessionFactoryTest extends DaoTestSupport {
    @Inject
    private SessionFactory sessionFactory;

    @Test
    public void config() {
        SqlSessionFactory writeable = this.sessionFactory.getWriteable("");
        Assert.assertNotNull(writeable);
        SqlSessionFactory readonly = this.sessionFactory.getReadonly("");
        Assert.assertEquals(writeable.hashCode(), readonly.hashCode());
    }

    @Test
    public void create() {
        Assert.assertNull(sessionFactory.getWriteable("new"));

        DaoUtil.createSessionFactory("new", sessionFactory);

        SqlSessionFactory writeable = this.sessionFactory.getWriteable("new");
        Assert.assertNotNull(writeable);
        SqlSessionFactory readonly = this.sessionFactory.getReadonly("new");
        Assert.assertNotEquals(writeable.hashCode(), readonly.hashCode());
    }
}
