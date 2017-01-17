package org.lpw.tephra.dao.dialect;

import org.springframework.stereotype.Repository;

/**
 * @author lpw
 */
@Repository("tephra.dao.dialect.mysql")
public class MysqlDialect implements Dialect {
    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public String getDriver() {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    public String getUrl(String ip, String schema) {
        return "jdbc:mysql://" + ip + "/" + schema + "?useUnicode=true&characterEncoding=utf8&autoReconnect=true";
    }

    @Override
    public String getValidationQuery() {
        return "SELECT CURRENT_DATE";
    }

    @Override
    public String getHibernateDialect() {
        return "org.hibernate.dialect.MySQLDialect";
    }

    @Override
    public void appendPagination(StringBuilder sql, int size, int page) {
        sql.append(" LIMIT ").append(size * (page - 1)).append(',').append(size);
    }
}
