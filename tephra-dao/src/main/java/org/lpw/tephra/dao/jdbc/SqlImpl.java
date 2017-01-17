package org.lpw.tephra.dao.jdbc;

import net.sf.json.JSONArray;
import org.lpw.tephra.dao.Mode;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author lpw
 */
@Repository("tephra.dao.jdbc.sql")
public class SqlImpl extends JdbcSupport<PreparedStatement> implements Sql {
    @Override
    public SqlTable query(String dataSource, String sql, Object[] args) {
        if (logger.isDebugEnable())
            logger.debug("执行SQL[{}:{}]检索操作。", sql, converter.toString(args));

        try {
            PreparedStatement pstmt = newPreparedStatement(dataSource, Mode.Read, sql);
            setArgs(pstmt, args);
            SqlTable sqlTable = query(pstmt.executeQuery());
            pstmt.close();

            return sqlTable;
        } catch (SQLException e) {
            logger.warn(e, "执行SQL[{}:{}]检索时发生异常！", sql, converter.toString(args));

            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONArray queryAsJson(String dataSource, String sql, Object[] args) {
        if (logger.isDebugEnable())
            logger.debug("执行SQL[{}:{}]检索操作。", sql, converter.toString(args));

        try {
            PreparedStatement pstmt = newPreparedStatement(dataSource, Mode.Read, sql);
            setArgs(pstmt, args);
            JSONArray array = queryAsJson(pstmt.executeQuery());
            pstmt.close();

            return array;
        } catch (SQLException e) {
            logger.warn(e, "执行SQL[{}:{}]检索时发生异常！", sql, converter.toString(args));

            throw new RuntimeException(e);
        }
    }

    @Override
    public int[] update(String sql, List<Object[]> args) {
        return update("", sql, args);
    }

    @Override
    public int[] update(String dataSource, String sql, List<Object[]> args) {
        if (logger.isDebugEnable())
            logger.debug("成功执行SQL[{}:{}]批量更新操作。", sql, converter.toString(args));

        if (validator.isEmpty(args))
            return new int[]{update(sql, new Object[0])};

        if (args.size() == 1)
            return new int[]{update(sql, args.get(0))};

        try {
            PreparedStatement pstmt = newPreparedStatement(dataSource, Mode.Write, sql);
            for (Object[] array : args) {
                setArgs(pstmt, array);
                pstmt.addBatch();
            }
            int[] array = pstmt.executeBatch();
            pstmt.close();

            return array;
        } catch (SQLException e) {
            logger.warn(e, "执行SQL[{}:{}]更新时发生异常！", sql, converter.toString(args));

            throw new RuntimeException(e);
        }
    }

    @Override
    public int backup(String from, String to, String where, Object[] args) {
        return backup("", from, to, where, args);
    }

    @Override
    public int backup(String dataSource, String from, String to, String where, Object[] args) {
        update(dataSource, "INSERT INTO " + to + " SELECT * FROM " + from + " WHERE " + where, args);

        return update(dataSource, "DELETE FROM " + from + " WHERE " + where, args);
    }

    protected PreparedStatement newPreparedStatement(String dataSource, Mode mode, String sql) throws SQLException {
        return getConnection(dataSource, mode).prepareStatement(sql);
    }
}
