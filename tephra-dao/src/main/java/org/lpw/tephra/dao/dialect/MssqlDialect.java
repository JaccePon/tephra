package org.lpw.tephra.dao.dialect;

import org.springframework.stereotype.Repository;

/**
 * @author lpw
 */
@Repository("tephra.dao.dialect.mssql")
public class MssqlDialect implements Dialect {
    @Override
    public String getName() {
        return "mssql";
    }

    @Override
    public String getDriver() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    @Override
    public String getUrl(String ip, String schema) {
        return "jdbc:sqlserver://" + ip + ";DatabaseName=" + schema;
    }

    @Override
    public String getValidationQuery() {
        return "SELECT GETDATE()";
    }

    @Override
    public String getHibernateDialect() {
        return "org.hibernate.dialect.SQLServerDialect";
    }

    @Override
    public void appendPagination(StringBuilder sql, int size, int page) {
        sql.insert(7, "ROW_NUMBER() AS RowNum,").insert(0, "SELECT * FROM(").append(") AS RowConstrainedResult WHERE RowNum>")
                .append(size * (page - 1)).append(" AND RowNum<=").append(size * page);
    }
}
