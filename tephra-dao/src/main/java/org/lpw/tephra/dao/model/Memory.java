package org.lpw.tephra.dao.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 内存表定义。
 *
 * @author lpw
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Memory {
    /**
     * 内存表名称。
     *
     * @return 内存表名称。
     */
    String name();
}
