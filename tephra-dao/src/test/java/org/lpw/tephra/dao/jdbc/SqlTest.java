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
