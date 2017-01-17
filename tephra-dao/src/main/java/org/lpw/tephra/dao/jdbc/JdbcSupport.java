package org.lpw.tephra.dao.jdbc;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.dao.Mode;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author lpw
 */
public abstract class JdbcSupport<T extends PreparedStatement> implements Jdbc {
    @Inject
    protected Validator validator;
    @Inject
    protected Converter converter;
    @Inject
    protected Logger logger;
    @Inject
    protected Connection connection;

    protected SqlTable query(ResultSet rs) throws SQLException {
        SqlTable sqlTable = BeanFactory.getBean(SqlTable.class);
        sqlTable.set(rs);
        rs.close();

        return sqlTable;
    }

    protected JSONArray queryAsJson(ResultSet rs) throws SQLException {
        JSONArray array = new JSONArray();
        String[] columns = new String[rs.getMetaData().getColumnCount()];
        for (int i = 0; i < columns.length; i++)
            columns[i] = rs.getMetaData().getColumnLabel(i + 1);
        for (; rs.next(); ) {
            JSONObject object = new JSONObject();
            for (String column : columns)
                object.put(column, converter.toString(rs.getObject(column)));
            array.add(object);
        }
        rs.close();

        return array;
    }

    @Override
    public int update(String dataSource, String sql, Object[] args) {
        if (logger.isDebugEnable())
            logger.debug("成功执行SQL[{}:{}]更新操作。", sql, converter.toString(args));

        try {
            T pstmt = newPreparedStatement(dataSource, Mode.Write, sql);
            setArgs(pstmt, args);
            int n = pstmt.executeUpdate();
            pstmt.close();

            return n;
        } catch (SQLException e) {
            logger.warn(e, "执行SQL[{}:{}]更新时发生异常！", sql, converter.toString(args));

            throw new RuntimeException(e);
        }
    }

    protected abstract T newPreparedStatement(String dataSource, Mode mode, String sql) throws SQLException;

    protected java.sql.Connection getConnection(String dataSource, Mode mode) throws SQLException {
        java.sql.Connection connection = this.connection.get(dataSource, mode);
        if (connection == null)
            throw new NullPointerException("无法获得数据库[" + mode + "]连接！");

        return connection;
    }

    /**
     * 设置参数集。
     *
     * @param pstmt PreparedStatement实例。
     * @param args  参数集。
     * @throws SQLException
     */
    protected void setArgs(T pstmt, Object[] args) throws SQLException {
        if (validator.isEmpty(args))
            return;

        for (int i = 0; i < args.length; i++)
            pstmt.setObject(i + 1, args[i]);
    }

    @Override
    public void fail(Throwable throwable) {
        connection.fail(throwable);
    }

    @Override
    public void close() {
        connection.close();
    }
}
