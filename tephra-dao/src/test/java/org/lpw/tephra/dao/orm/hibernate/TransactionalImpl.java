package org.lpw.tephra.dao.orm.hibernate;

import org.junit.Assert;
import org.lpw.tephra.dao.DaoUtil;
import org.lpw.tephra.dao.Mode;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Repository("tephra.dao.orm.hibernate.transactional.impl")
public class TransactionalImpl {
    @Inject
    private SessionFactory sessionFactory;
    @Inject
    private Session session;

    @javax.transaction.Transactional
    public void get() {
        session.beginTransaction();
        org.hibernate.Session session1 = session.get(null, Mode.Write);
        Assert.assertNotNull(session1);
        org.hibernate.Session session2 = session.get(null, Mode.Read);
        Assert.assertEquals(session1.hashCode(), session2.hashCode());

        DaoUtil.createSessionFactory("HasReadonlyTransactional", sessionFactory);
        org.hibernate.Session session3 = session.get("HasReadonlyTransactional", Mode.Write);
        Assert.assertNotNull(session3);
        org.hibernate.Session session4 = session.get("HasReadonlyTransactional", Mode.Read);
        Assert.assertEquals(session3.hashCode(), session4.hashCode());
    }
}
