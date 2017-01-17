# ORM接口说明
ORM包提供了完整实现JPA规范的Hibernate接口，同时针对需要更高性能的ORM需求提供了一个简单的ORM工具，同时定义了检索条件及分页操作规范。

1、Query
```java
package org.lpw.tephra.dao.orm;

import org.lpw.tephra.dao.model.Model;

/**
 * 检索接口。
 *
 * @author lpw
 */
public interface Query {
    /**
     * 获取Model类。
     *
     * @return Model类。
     */
    Class<? extends Model> getModelClass();

    /**
     * 获取数据源。
     *
     * @return 数据源。
     */
    String getDataSource();

    /**
     * 获取SET片段。
     *
     * @return SET片段。
     */
    String getSet();

    /**
     * 获取WHERE片段。
     *
     * @return WHERE片段。
     */
    String getWhere();

    /**
     * 获取GROUP BY片段。
     *
     * @return GROUP BY片段。
     */
    String getGroup();

    /**
     * 获取ORDER BY片段。
     *
     * @return ORDER BY片段。
     */
    String getOrder();

    /**
     * 是否加锁。
     *
     * @return 如果已加锁则返回true；否则返回false。
     */
    boolean isLocked();

    /**
     * 获取最大返回的记录数。
     *
     * @return 最大返回的记录数。
     */
    int getSize();

    /**
     * 获取当前显示的页码。
     *
     * @return 当前显示的页码。
     */
    int getPage();
}
```
2、PageList
```java
package org.lpw.tephra.dao.orm;

import net.sf.json.JSONObject;
import org.lpw.tephra.dao.model.Model;

import java.util.List;

/**
 * @author lpw
 */
public interface PageList<T extends Model> {
    /**
     * 设置分页信息。
     *
     * @param count  记录总数。
     * @param size   每页显示记录数。
     * @param number 当前显示页码数。
     */
    void setPage(int count, int size, int number);

    /**
     * 获取记录总数。
     *
     * @return 记录总数。
     */
    int getCount();

    /**
     * 获取每页最大显示记录数。
     *
     * @return 每页最大显示记录数。
     */
    int getSize();

    /**
     * 获取当前显示页数。
     *
     * @return 当前显示页数。
     */
    int getNumber();

    /**
     * 获取总页数。
     *
     * @return 总页数。
     */
    int getPage();

    /**
     * 获取分页显示起始页数。
     *
     * @return 起始页数。
     */
    int getPageStart();

    /**
     * 获取分页显示结束页数。
     *
     * @return 结束页数。
     */
    int getPageEnd();

    /**
     * 获取数据集。
     *
     * @return 数据集。
     */
    List<T> getList();

    /**
     * 设置数据集。
     *
     * @param list 数据集。
     */
    void setList(List<T> list);

    /**
     * 转化为JSON格式的数据。
     *
     * @return JSON格式的数据。
     */
    JSONObject toJson();
}
```
3、Orm
```java
package org.lpw.tephra.dao.orm;

import org.lpw.tephra.dao.Commitable;
import org.lpw.tephra.dao.model.Model;

import java.util.Collection;

/**
 * ORM接口。
 *
 * @author lpw
 */
public interface Orm<Q extends Query> extends Commitable {
    /**
     * 根据ID值获取Model实例。
     *
     * @param modelClass Model类。
     * @param id         ID值。
     * @return Model实例，如果不存在则返回null。
     */
    <T extends Model> T findById(Class<T> modelClass, String id);

    /**
     * 根据ID值获取Model实例。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param modelClass Model类。
     * @param id         ID值。
     * @return Model实例，如果不存在则返回null。
     */
    <T extends Model> T findById(String dataSource, Class<T> modelClass, String id);

    /**
     * 根据ID值获取Model实例。
     *
     * @param modelClass Model类。
     * @param id         ID值。
     * @param lock       是否加悲观锁。
     * @return Model实例，如果不存在则返回null。
     */
    <T extends Model> T findById(Class<T> modelClass, String id, boolean lock);

    /**
     * 根据ID值获取Model实例。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param modelClass Model类。
     * @param id         ID值。
     * @param lock       是否加悲观锁。
     * @return Model实例，如果不存在则返回null。
     */
    <T extends Model> T findById(String dataSource, Class<T> modelClass, String id, boolean lock);

    /**
     * 检索一条满足条件的数据。如果存在多条满足条件的数据则只返回第一条数据。
     *
     * @param query 检索条件。
     * @param args  参数集。
     * @return Model实例，如果不存在则返回null。
     */
    <T extends Model> T findOne(Q query, Object[] args);

    /**
     * 检索满足条件的数据。
     *
     * @param query 检索条件。
     * @param args  参数集。
     * @return Model实例集。
     */
    <T extends Model> PageList<T> query(Q query, Object[] args);

    /**
     * 计算满足条件的数据数。
     *
     * @param query 检索条件。
     * @param args  参数集。
     * @return 数据数。
     */
    int count(Q query, Object[] args);

    /**
     * 保存Model。
     * 如果要保存Model实例的ID为null则执行新增操作，否则执行更新操作。新增时将自动创建一个随机ID。
     *
     * @param model 要保存的Model。
     * @return 如果保存成功则返回true；否则返回false。
     */
    <T extends Model> boolean save(T model);

    /**
     * 保存Model。
     * 如果要保存Model实例的ID为null则执行新增操作，否则执行更新操作。新增时将自动创建一个随机ID。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param model      要保存的Model。
     * @return 如果保存成功则返回true；否则返回false。
     */
    <T extends Model> boolean save(String dataSource, T model);

    /**
     * 保存Model集。
     * 如果要保存Model实例的ID为null则执行新增操作，否则执行更新操作。新增时将自动创建一个随机ID。
     *
     * @param models 要保存的Model集。
     * @param <T>    Model类。
     */
    <T extends Model> void save(Collection<T> models);

    /**
     * 保存Model集。
     * 如果要保存Model实例的ID为null则执行新增操作，否则执行更新操作。新增时将自动创建一个随机ID。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param models     要保存的Model集。
     * @param <T>        Model类。
     */
    <T extends Model> void save(String dataSource, Collection<T> models);

    /**
     * 新增Model。ID由业务系统或数据库控制。
     *
     * @param model 要保存的Model。
     * @return 如果保存成功则返回true；否则返回false。
     */
    <T extends Model> boolean insert(T model);

    /**
     * 新增Model。ID由业务系统或数据库控制。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param model      要保存的Model。
     * @return 如果保存成功则返回true；否则返回false。
     */
    <T extends Model> boolean insert(String dataSource, T model);

    /**
     * 批量更新数据。
     *
     * @param query 更新条件。
     * @param args  参数集。
     * @return 如果删除成功则返回true；否则返回false。
     */
    boolean update(Q query, Object[] args);

    /**
     * 删除Model。
     *
     * @param model 要删除的Model。
     * @return 如果删除成功则返回true；否则返回false。
     */
    <T extends Model> boolean delete(T model);

    /**
     * 删除Model。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param model      要删除的Model。
     * @return 如果删除成功则返回true；否则返回false。
     */
    <T extends Model> boolean delete(String dataSource, T model);

    /**
     * 批量删除数据。
     *
     * @param query 删除条件。
     * @param args  参数集。
     * @return 如果删除成功则返回true；否则返回false。
     */
    boolean delete(Q query, Object[] args);

    /**
     * 删除指定ID值的Model实例。
     *
     * @param modelClass Model类。
     * @param id         ID值。
     * @return 如果删除成功则返回true；否则返回false。
     */
    <T extends Model> boolean deleteById(Class<T> modelClass, String id);

    /**
     * 删除指定ID值的Model实例。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param modelClass Model类。
     * @param id         ID值。
     * @return 如果删除成功则返回true；否则返回false。
     */
    <T extends Model> boolean deleteById(String dataSource, Class<T> modelClass, String id);
}
```
## 添加悲观锁
ORM提供的锁为悲观锁，并且当使用悲观锁检索数据时，将自动开启写事务，即此时将不处理读写分离，而只使用可写连接。

建议：添加悲观锁时应充分考虑性能影响，以及死锁风险。