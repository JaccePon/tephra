package org.lpw.tephra.dao.dialect;

/**
 * 数据库方言。
 *
 * @author lpw
 */
public interface Dialect {
    /**
     * 获取方言名称。
     *
     * @return 方言名称。
     */
    String getName();

    /**
     * 获取驱动类名称。
     *
     * @return 驱动类名称。
     */
    String getDriver();

    /**
     * 获取访问URL地址。
     *
     * @param ip     IP地址。
     * @param schema 数据库名称。
     * @return 访问URL地址。
     */
    String getUrl(String ip, String schema);

    /**
     * 获取验证SQL语句。
     *
     * @return 验证SQL语句。
     */
    String getValidationQuery();

    /**
     * 获取Hibernate使用的数据库方言类名称。
     *
     * @return Hibernate使用的数据库方言类名称。
     */
    String getHibernateDialect();

    /**
     * 添加分页设置。
     *
     * @param sql  SQL语句。
     * @param size 每页显示记录数。
     * @param page 当前显示页码值。
     */
    void appendPagination(StringBuilder sql, int size, int page);

    /**
     * 获取LIKE参数值。
     *
     * @param like   参数值。
     * @param prefix 是否模糊匹配前部字符串，即是否在参数值前添加%。
     * @param suffix 是否模糊匹配尾部字符串，即是否在参数值末添加%。
     * @return LIKE参数值。
     */
    default String getLike(String like, boolean prefix, boolean suffix) {
        StringBuilder sb = new StringBuilder();
        if (prefix)
            sb.append('%');
        for (char ch : like.toCharArray()) {
            if (ch == '%')
                sb.append("[%]");
            else
                sb.append(ch);
        }
        if (suffix)
            sb.append('%');

        return sb.toString();
    }
}
