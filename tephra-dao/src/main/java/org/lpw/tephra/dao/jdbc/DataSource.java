package org.lpw.tephra.dao.jdbc;

import org.lpw.tephra.dao.ConnectionFactory;
import org.lpw.tephra.dao.dialect.Dialect;

import java.util.List;
import java.util.Map;

/**
 * @author lpw
 */
public interface DataSource extends ConnectionFactory<javax.sql.DataSource> {
    /**
     * 获取只读数据源集。
     *
     * @param name 数据源引用名称。
     * @return 只读数据源集；如果不存在则返回null。
     */
    List<javax.sql.DataSource> listReadonly(String name);

    /**
     * 验证是否包含只读数据源。
     *
     * @param name 数据源引用名称。
     * @return 如果包含只读数据源则返回true；否则返回false。
     */
    boolean hasReadonly(String name);

    /**
     * 获取数据库方言。
     *
     * @return 数据库方言。
     */
    Map<String, Dialect> getDialects();

    /**
     * 获取配置所使用的方言。
     *
     * @param key 配置key。
     * @return 方言实例。
     */
    Dialect getDialect(String key);
}
