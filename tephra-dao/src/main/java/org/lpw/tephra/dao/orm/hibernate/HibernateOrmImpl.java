package org.lpw.tephra.dao.orm.hibernate;

import org.hibernate.LockOptions;
import org.hibernate.query.Query;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.dao.Mode;
import org.lpw.tephra.dao.model.Model;
import org.lpw.tephra.dao.orm.OrmSupport;
import org.lpw.tephra.dao.orm.PageList;
import org.lpw.tephra.util.Converter;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author lpw
 */
@Repository("tephra.dao.orm.hibernate")
public class HibernateOrmImpl extends OrmSupport<HibernateQuery> implements HibernateOrm {
    private static final String[] ARG = {"?", ":arg", "arg"};

    @Inject
    private Converter converter;
    @Inject
    private Session session;

    @Override
    public <T extends Model> T findById(String dataSource, Class<T> modelClass, String id, boolean lock) {
        if (validator.isEmpty(id))
            return null;

        if (lock) {
            session.beginTransaction();

            return session.get(dataSource, Mode.Write).get(modelClass, id, LockOptions.UPGRADE);
        }

        return session.get(dataSource, Mode.Read).get(modelClass, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Model> T findOne(HibernateQuery query, Object[] args) {
        query.size(1).page(1);
        Iterator<T> iterator = createQuery(query.getDataSource(), Mode.Read, getQueryHql(query), args, query.isLocked(), 1, 1).iterate();

        return iterator.hasNext() ? iterator.next() : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Model> PageList<T> query(HibernateQuery query, Object[] args) {
        PageList<T> models = BeanFactory.getBean(PageList.class);
        if (query.getSize() > 0)
            models.setPage(count(query, args), query.getSize(), query.getPage());
        models.setList(createQuery(query.getDataSource(), Mode.Read, getQueryHql(query), args, query.isLocked(), models.getSize(), models.getNumber()).list());

        return models;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Model> Iterator<T> iterate(HibernateQuery query, Object[] args) {
        return createQuery(query.getDataSource(), Mode.Read, getQueryHql(query), args, query.isLocked(), query.getSize(), query.getPage()).iterate();
    }

    private StringBuilder getQueryHql(HibernateQuery query) {
        StringBuilder hql = from(new StringBuilder().append("FROM "), query);
        if (!validator.isEmpty(query.getWhere()))
            hql.append(" WHERE ").append(query.getWhere());
        if (!validator.isEmpty(query.getGroup()))
            hql.append(" GROUP BY ").append(query.getGroup());
        if (!validator.isEmpty(query.getOrder()))
            hql.append(" ORDER BY ").append(query.getOrder());

        return hql;
    }

    @Override
    public int count(HibernateQuery query, Object[] args) {
        StringBuilder hql = from(new StringBuilder().append("SELECT COUNT(*) FROM "), query);
        if (!validator.isEmpty(query.getWhere()))
            hql.append(" WHERE ").append(query.getWhere());
        if (!validator.isEmpty(query.getGroup()))
            hql.append(" GROUP BY ").append(query.getGroup());

        return converter.toInt(createQuery(query.getDataSource(), Mode.Read, hql, args, query.isLocked(), 0, 0).iterate().next());
    }

    @Override
    public <T extends Model> boolean save(String dataSource, T model) {
        if (model == null) {
            logger.warn(null, "要保存的Model为null！");

            return false;
        }

        if (validator.isEmpty(model.getId()))
            model.setId(null);
        session.get(dataSource, Mode.Write).saveOrUpdate(model);

        return true;
    }

    @Override
    public <T extends Model> boolean insert(String dataSource, T model) {
        if (model == null) {
            logger.warn(null, "要保存的Model为null！");

            return false;
        }

        session.get(dataSource, Mode.Write).save(model);

        return true;
    }

    @Override
    public boolean update(HibernateQuery query, Object[] args) {
        StringBuilder hql = from(new StringBuilder().append("UPDATE "), query).append(" SET ").append(query.getSet());
        if (!validator.isEmpty(query.getWhere()))
            hql.append(" WHERE ").append(query.getWhere());

        createQuery(query.getDataSource(), Mode.Write, hql, args, query.isLocked(), 0, 0).executeUpdate();

        return true;
    }

    @Override
    public <T extends Model> boolean delete(String dataSource, T model) {
        session.get(dataSource, Mode.Write).delete(model);

        return true;
    }

    @Override
    public boolean delete(HibernateQuery query, Object[] args) {
        StringBuilder hql = from(new StringBuilder().append("DELETE "), query);
        if (!validator.isEmpty(query.getWhere()))
            hql.append(" WHERE ").append(query.getWhere());

        createQuery(query.getDataSource(), Mode.Write, hql, args, query.isLocked(), 0, 0).executeUpdate();

        return true;
    }

    @Override
    public <T extends Model> boolean deleteById(String dataSource, Class<T> modelClass, String id) {
        return delete(new HibernateQuery(modelClass).dataSource(dataSource).where("id=?"), new Object[]{id});
    }

    private StringBuilder from(StringBuilder hql, HibernateQuery query) {
        return hql.append(query.getModelClass().getName());
    }

    private Query createQuery(String dataSource, Mode mode, StringBuilder hql, Object[] args, boolean lock, int size, int page) {
        if (logger.isDebugEnable())
            logger.debug("hql:{};args:{}", hql, converter.toString(args));

        Query query = session.get(dataSource, mode).createQuery(replaceArgs(hql));
        if (lock) {
            session.beginTransaction();
            query.setLockOptions(LockOptions.UPGRADE);
        }
        if (size > 0)
            query.setFirstResult(size * (page - 1)).setMaxResults(size);

        if (validator.isEmpty(args))
            return query;

        for (int i = 0; i < args.length; i++) {
            if (args[i] == null)
                query.setParameter(ARG[2] + i, args[i]);
            else if (args[i] instanceof Collection<?>)
                query.setParameterList(ARG[2] + i, (Collection<?>) args[i]);
            else if (args[i].getClass().isArray())
                query.setParameterList(ARG[2] + i, (Object[]) args[i]);
            else
                query.setParameter(ARG[2] + i, args[i]);
        }

        return query;
    }

    private String replaceArgs(StringBuilder hql) {
        for (int i = 0, position; (position = hql.indexOf(ARG[0])) > -1; i++)
            hql.replace(position, position + 1, ARG[1] + i);

        return hql.toString();
    }

    @Override
    public void fail(Throwable throwable) {
        session.fail(throwable);
    }

    @Override
    public void close() {
        session.close();
    }
}
