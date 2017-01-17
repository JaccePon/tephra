package org.lpw.tephra.dao.jdbc;

import net.sf.json.JSONArray;
import org.lpw.tephra.dao.Mode;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author lpw
 */
@Repository("tephra.dao.jdbc.procedure")
public class ProcedureImpl extends JdbcSupport<CallableStatement> implements Procedure {
    @Override
    public SqlTable query(String dataSource, String sql, Object[] args) {
        if (logger.isDebugEnable())
            logger.debug("执行SQL[{}:{}]检索操作。", sql, converter.toString(args));

        try {
            CallableStatement pstmt = newPreparedStatement(dataSource, Mode.Read, sql);
            setArgs(pstmt, args);
            int index = (validator.isEmpty(args) ? 0 : args.length) + 1;
            pstmt.registerOutParameter(index, Types.REF_CURSOR);
            pstmt.execute();
            SqlTable sqlTable = query((ResultSet) pstmt.getObject(index));
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
            CallableStatement pstmt = newPreparedStatement(dataSource, Mode.Read, sql);
            setArgs(pstmt, args);
            int index = (validator.isEmpty(args) ? 0 : args.length) + 1;
            pstmt.registerOutParameter(index, Types.REF_CURSOR);
            pstmt.execute();
            JSONArray array = queryAsJson((ResultSet) pstmt.getObject(index));
            pstmt.close();

            return array;
        } catch (SQLException e) {
            logger.warn(e, "执行SQL[{}:{}]检索时发生异常！", sql, converter.toString(args));

            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T queryObject(String sql, Object[] args) {
        return queryObject("", sql, args);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T> T queryObject(String dataSource, String sql, Object[] args) {
        if (logger.isDebugEnable())
            logger.debug("执行SQL[{}:{}]检索操作。", sql, converter.toString(args));

        try {
            CallableStatement pstmt = newPreparedStatement(dataSource, Mode.Read, sql);
            setArgs(pstmt, args);
            int index = (validator.isEmpty(args) ? 0 : args.length) + 1;
            pstmt.registerOutParameter(index, Types.JAVA_OBJECT);
            pstmt.execute();
            T object = (T) pstmt.getObject(index);
            pstmt.close();

            return object;
        } catch (SQLException e) {
            logger.warn(e, "执行SQL[{}:{}]检索时发生异常！", sql, converter.toString(args));

            throw new RuntimeException(e);
        }
    }

    protected CallableStatement newPreparedStatement(String dataSource, Mode mode, String sql) throws SQLException {
        return getConnection(dataSource, mode).prepareCall(sql);
    }
}
