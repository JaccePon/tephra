# 执行SQL
Tephra提供了直接执行SQL脚本的能力，可以执行普通SQL，也可以执行存储过程，相关接口描述如下：

1、Jdbc
```java
package org.lpw.tephra.dao.jdbc;

import net.sf.json.JSONArray;

/**
 * JDBC操作接口。
 *
 * @author lpw
 */
public interface Jdbc {
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

    /**
     * 手动回滚本次事务所有更新操作。
     */
    void rollback();

    /**
     * 提交持久化，并关闭当前线程的所有连接。
     */
    void close();
}
```
2、SQL
```java
package org.lpw.tephra.dao.jdbc;

import java.util.List;

/**
 * SQL操作接口。
 *
 * @author lpw
 */
public interface Sql extends Jdbc {
    /**
     * 执行批量更新操作。
     *
     * @param sql  SQL。
     * @param args 参数集。
     * @return 影响记录数。
     */
    int[] update(String sql, List<Object[]> args);

    /**
     * 执行批量更新操作。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param sql        SQL。
     * @param args       参数集。
     * @return 影响记录数。
     */
    int[] update(String dataSource, String sql, List<Object[]> args);

    /**
     * 备份数据。
     * 将数据从源表移动到目标表中。
     *
     * @param from  源表名称。
     * @param to    目标表名称。
     * @param where 数据WHERE字句。
     * @param args  参数集。
     * @return 备份记录数。
     */
    int backup(String from, String to, String where, Object[] args);

    /**
     * 备份数据。
     * 将数据从源表移动到目标表中。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param from       源表名称。
     * @param to         目标表名称。
     * @param where      数据WHERE字句。
     * @param args       参数集。
     * @return 备份记录数。
     */
    int backup(String dataSource, String from, String to, String where, Object[] args);
}
```
3、存储过程
```java
package org.lpw.tephra.dao.jdbc;

/**
 * 存储过程操作接口。
 *
 * @author lpw
 */
public interface Procedure extends Jdbc {
    /**
     * 执行检索操作。
     *
     * @param sql  SQL。
     * @param args 参数集。
     * @return 数据集。
     */
    <T> T queryObject(String sql, Object[] args);

    /**
     * 执行检索操作。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param sql        SQL。
     * @param args       参数集。
     * @return 数据集。
     */
    <T> T queryObject(String dataSource, String sql, Object[] args);
}
```
4、简单的增删改查示例：
```java
package org.lpw.tephra.dao.jdbc;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.DaoTestSupport;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.TimeUnit;

import javax.inject.Inject;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author lpw
 */
public class SqlTest extends DaoTestSupport {
    @Inject
    private Converter converter;
    @Inject
    private Sql sql;

    @Test
    public void crud() {
        long time = System.currentTimeMillis();
        SqlTable table = sql.query("select * from t_tephra_test", null);
        Assert.assertNotNull(table);
        Assert.assertEquals(0, table.getRowCount());
        Assert.assertEquals(5, table.getColumnCount());

        for (int i = 0; i < 9; i++)
            sql.update("insert into t_tephra_test values(?,?,?,?,?);", new Object[]{"id" + i, i, "name" + i,
                    new Date(time - i * TimeUnit.Day.getTime()), new Timestamp(time - i * TimeUnit.Hour.getTime())});
        table = sql.query("select * from t_tephra_test order by c_sort", null);
        Assert.assertEquals(9, table.getRowCount());
        Assert.assertEquals(5, table.getColumnCount());
        check(table, 0, 0, time);

        sql.update("update t_tephra_test set c_name=? where c_id=?;", new Object[]{"tephra", "id0"});
        table = sql.query("select * from t_tephra_test order by c_sort", null);
        Assert.assertEquals(9, table.getRowCount());
        Assert.assertEquals(5, table.getColumnCount());
        Assert.assertEquals("id0", table.get(0, 0));
        Assert.assertEquals(0, converter.toInt(table.get(0, 1)));
        Assert.assertEquals("tephra", table.get(0, 2));
        Assert.assertEquals("id0", table.get(0, "c_id"));
        Assert.assertEquals(0, converter.toInt(table.get(0, "c_sort")));
        Assert.assertEquals("tephra", table.get(0, "c_name"));
        check(table, 1, 0, time);

        sql.update("delete from t_tephra_test where c_id=?;", new Object[]{"id0"});
        table = sql.query("select * from t_tephra_test order by c_sort", null);
        Assert.assertEquals(8, table.getRowCount());
        Assert.assertEquals(5, table.getColumnCount());
        check(table, 1, 1, time);

        sql.update("delete from t_tephra_test;", new Object[0]);
        table = sql.query("select * from t_tephra_test order by c_sort", null);
        Assert.assertEquals(0, table.getRowCount());
        Assert.assertEquals(5, table.getColumnCount());

        sql.close();
    }

    private void check(SqlTable table, int start, int off, long time) {
        for (int i = start; i < 9 - off; i++) {
            Assert.assertEquals("id" + (i + off), table.get(i, 0));
            Assert.assertEquals(i + off, converter.toInt(table.get(i, 1)));
            Assert.assertEquals("name" + (i + off), table.get(i, 2));
            Assert.assertEquals("id" + (i + off), table.get(i, "c_id"));
            Assert.assertEquals(i + off, converter.toInt(table.get(i, "c_sort")));
            Assert.assertEquals("name" + (i + off), table.get(i, "c_name"));
            Assert.assertEquals(converter.toString(new Date(time - (i + off) * TimeUnit.Day.getTime())), converter.toString(table.get(i, "c_date")));
            Assert.assertEquals(converter.toString(new Timestamp(time - (i + off) * TimeUnit.Hour.getTime())), converter.toString(table.get(i, "c_time")));
        }
    }
}
```