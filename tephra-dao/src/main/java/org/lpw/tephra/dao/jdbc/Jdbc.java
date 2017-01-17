package org.lpw.tephra.dao.jdbc;

import net.sf.json.JSONArray;
import org.lpw.tephra.atomic.Atomicable;

/**
 * JDBC操作接口。
 *
 * @author lpw
 */
public interface Jdbc extends Atomicable {
    /**
     * 执行检索操作。
     *
     * @param sql  SQL。
     * @param args 参数集。
     * @return 数据集。
     */
    default SqlTable query(String sql, Object[] args) {
        return query("", sql, args);
    }

    /**
     * 执行检索操作。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param sql        SQL。
     * @param args       参数集。
     * @return 数据集。
     */
    SqlTable query(String dataSource, String sql, Object[] args);

    /**
     * 执行检索操作，并将结果集以JSON数组格式返回。
     *
     * @param sql  SQL。
     * @param args 参数集。
     * @return 数据集。
     */
    default JSONArray queryAsJson(String sql, Object[] args) {
        return queryAsJson("", sql, args);
    }

    /**
     * 执行检索操作，并将结果集以JSON数组格式返回。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param sql        SQL。
     * @param args       参数集。
     * @return 数据集。
     */
    JSONArray queryAsJson(String dataSource, String sql, Object[] args);

    /**
     * 执行更新操作。
     *
     * @param sql  SQL。
     * @param args 参数集。
     * @return 影响记录数。
     */
    default int update(String sql, Object[] args) {
        return update("", sql, args);
    }

    /**
     * 执行更新操作。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param sql        SQL。
     * @param args       参数集。
     * @return 影响记录数。
     */
    int update(String dataSource, String sql, Object[] args);
}
