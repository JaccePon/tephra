package org.lpw.tephra.dao.dialect;

/**
 * 方言工厂。
 *
 * @author lpw
 */
public interface DialectFactory {
    /**
     * 获取方言实例。
     *
     * @param name 方言名称。
     * @return 方言实例。
     */
    Dialect get(String name);
}
