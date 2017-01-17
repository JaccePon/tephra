package org.lpw.tephra.dao.orm;

import org.lpw.tephra.dao.model.Model;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;

import javax.inject.Inject;
import java.util.Collection;

/**
 * ORM支持类。
 *
 * @author lpw
 */
public abstract class OrmSupport<Q extends Query> implements Orm<Q> {
    @Inject
    protected Validator validator;
    @Inject
    protected Logger logger;

    @Override
    public <T extends Model> T findById(Class<T> modelClass, String id) {
        return findById("", modelClass, id, false);
    }

    @Override
    public <T extends Model> T findById(String dataSource, Class<T> modelClass, String id) {
        return findById(dataSource, modelClass, id, false);
    }

    @Override
    public <T extends Model> T findById(Class<T> modelClass, String id, boolean lock) {
        return findById("", modelClass, id, lock);
    }

    @Override
    public <T extends Model> boolean save(T model) {
        return save("", model);
    }

    @Override
    public <T extends Model> void save(Collection<T> models) {
        save("", models);
    }

    @Override
    public <T extends Model> void save(String dataSource, Collection<T> models) {
        if (validator.isEmpty(models))
            return;

        models.forEach(model -> save(dataSource, model));

        if (logger.isDebugEnable())
            logger.debug("批量保存[{}]个Model数据。", models.size());
    }

    @Override
    public <T extends Model> boolean insert(T model) {
        return insert("", model);
    }

    @Override
    public <T extends Model> boolean delete(T model) {
        return delete("", model);
    }

    @Override
    public <T extends Model> boolean deleteById(Class<T> modelClass, String id) {
        return deleteById("", modelClass, id);
    }
}
