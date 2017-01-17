package org.lpw.tephra.dao.dialect;

import org.springframework.stereotype.Repository;

/**
 * @author lpw
 */
@Repository("tephra.dao.dialect.oracle")
public class OracleDialect implements Dialect {
    @Override
    public String getName() {
        return "oracle";
    }

    @Override
    public String getDriver() {
        return "oracle.jdbc.driver.OracleDriver";
    }

    @Override
    public String getUrl(String ip, String schema) {
        if (ip.charAt(0) == '/')
            return "jdbc:oracle:thin:@" + ip + "/" + schema;

        return "jdbc:oracle:thin:@" + ip + ":" + schema;
    }

    @Override
    public String getValidationQuery() {
        return "SELECT SYSDATE FROM DUAL";
    }

    @Override
    public String getHibernateDialect() {
        return "org.hibernate.dialect.Oracle10gDialect";
    }

    @Override
    public void appendPagination(StringBuilder sql, int size, int page) {
        sql.insert(0, "SELECT * FROM (SELECT oracle_pagination_1.*, ROWNUM AS rowno FROM (").append(") oracle_pagination_1 WHERE ROWNUM<=").append(size * page)
                .append(") oracle_pagination_2 WHERE oracle_pagination_2.rowno>").append(size * (page - 1));
    }
}
