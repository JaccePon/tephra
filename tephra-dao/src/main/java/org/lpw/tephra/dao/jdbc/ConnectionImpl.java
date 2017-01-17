package org.lpw.tephra.dao.jdbc;

import org.lpw.tephra.dao.ConnectionSupport;
import org.lpw.tephra.dao.Mode;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
@Repository("tephra.dao.jdbc.connection")
public class ConnectionImpl extends ConnectionSupport<Connection> implements org.lpw.tephra.dao.jdbc.Connection {
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    @Inject
    private org.lpw.tephra.dao.jdbc.DataSource dataSource;
    private ThreadLocal<Map<String, Connection>> connections = new ThreadLocal<>();
    private ThreadLocal<Boolean> transactional = new ThreadLocal<>();

    @Override
    public void beginTransaction() {
        transactional.set(true);
    }

    @Override
    public Connection get(String dataSource, Mode mode) {
        if (dataSource == null)
            dataSource = "";
        if ((transactional.get() != null && transactional.get()) || !this.dataSource.hasReadonly(dataSource))
            mode = Mode.Write;
        Map<String, Connection> connections = this.connections.get();
        if (connections == null)
            connections = new HashMap<>();
        String key = dataSource + mode.ordinal();
        Connection connection = connections.get(key);
        if (connection != null)
            return connection;

        try {
            DataSource ds = null;
            if (mode == Mode.Read)
                ds = this.dataSource.getReadonly(dataSource);
            if (ds == null)
                ds = this.dataSource.getWriteable(dataSource);
            connection = ds.getConnection();
            connection.setAutoCommit(mode == Mode.Read);
            connections.put(key, connection);
            this.connections.set(connections);

            return connection;
        } catch (Exception e) {
            logger.warn(e, "获取数据库[{}:{}]连接时发生异常！", dataSource, mode);

            throw new NullPointerException("获取数据库[" + dataSource + "," + mode + "]连接时发生异常！");
        }
    }

    @Override
    public void fail(Throwable throwable) {
        Map<String, Connection> connections = this.connections.get();
        if (validator.isEmpty(connections))
            return;

        connections.forEach((key, connection) -> {
            try {
                if (!connection.isClosed()) {
                    if (!connection.getAutoCommit())
                        connection.rollback();
                    connection.close();
                }
            } catch (SQLException e) {
                logger.warn(e, "回滚数据库连接时发生异常！");
            }
        });

        if (logger.isDebugEnable())
            logger.debug("回滚[{}]个数据库连接！", connections.size());

        this.connections.remove();
        transactional.remove();
    }

    @Override
    public void close() {
        Map<String, Connection> connections = this.connections.get();
        if (validator.isEmpty(connections))
            return;

        connections.forEach((key, connection) -> {
            try {
                if (!connection.isClosed()) {
                    if (!connection.getAutoCommit())
                        connection.commit();
                    connection.close();
                }
            } catch (SQLException e) {
                logger.warn(e, "关闭数据库连接时发生异常！");

                fail(e);
            }
        });

        if (logger.isDebugEnable())
            logger.debug("关闭[{}]个数据库连接！", connections.size());

        this.connections.remove();
        transactional.remove();
    }
}
