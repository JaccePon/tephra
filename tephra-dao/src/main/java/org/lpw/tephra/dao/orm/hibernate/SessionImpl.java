package org.lpw.tephra.dao.orm.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.lpw.tephra.dao.ConnectionSupport;
import org.lpw.tephra.dao.Mode;
import org.lpw.tephra.dao.jdbc.DataSource;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
@Repository("tephra.dao.orm.hibernate.session")
public class SessionImpl extends ConnectionSupport<Session> implements org.lpw.tephra.dao.orm.hibernate.Session {
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    @Inject
    private DataSource dataSource;
    @Inject
    private org.lpw.tephra.dao.orm.hibernate.SessionFactory sessionFactory;
    private ThreadLocal<Map<String, Session>> sessions = new ThreadLocal<>();
    private ThreadLocal<Boolean> transactional = new ThreadLocal<>();

    @Override
    public void beginTransaction() {
        transactional.set(true);
    }

    @Override
    public Session get(String dataSource, Mode mode) {
        if (dataSource == null)
            dataSource = "";
        if ((transactional.get() != null && transactional.get()) || !this.dataSource.hasReadonly(dataSource))
            mode = Mode.Write;
        Map<String, Session> sessions = this.sessions.get();
        if (sessions == null)
            sessions = new HashMap<>();
        String key = dataSource + mode.ordinal();
        Session session = sessions.get(key);
        if (session != null)
            return session;

        SessionFactory sessionFactory = null;
        if (mode == Mode.Read)
            sessionFactory = this.sessionFactory.getReadonly(dataSource);
        if (sessionFactory == null)
            sessionFactory = this.sessionFactory.getWriteable(dataSource);
        if (sessionFactory == null)
            throw new NullPointerException("无法获得[" + mode + "]Hibernate环境！");

        session = sessionFactory.getCurrentSession();
        if (!session.getTransaction().isActive())
            session.beginTransaction();
        sessions.put(key, session);
        this.sessions.set(sessions);

        return session;
    }

    @Override
    public void fail(Throwable throwable) {
        Map<String, Session> sessions = this.sessions.get();
        if (validator.isEmpty(sessions))
            return;

        sessions.forEach((key, session) -> session.getTransaction().rollback());
        this.sessions.remove();
        transactional.remove();

        if (logger.isDebugEnable())
            logger.debug("回滚[{}]Hibernate Session！", sessions.size());
    }

    @Override
    public void close() {
        Map<String, Session> sessions = this.sessions.get();
        if (validator.isEmpty(sessions))
            return;

        sessions.forEach((key, session) -> {
            if (session.getTransaction().isActive())
                session.getTransaction().commit();
        });
        this.sessions.remove();
        transactional.remove();

        if (logger.isDebugEnable())
            logger.debug("关闭[{}]Hibernate Session！", sessions.size());
    }
}
