package org.lpw.tephra.dao.orm.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.lpw.tephra.dao.ConnectionFactory;

/**
 * @author lpw
 */
public interface SessionFactory extends ConnectionFactory<SqlSessionFactory> {
}
