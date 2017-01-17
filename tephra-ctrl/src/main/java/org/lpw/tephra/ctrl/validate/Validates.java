package org.lpw.tephra.ctrl.validate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 验证规则集。
 *
 * @author lpw
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Validates {
    /**
     * 验证规则集。
     *
     * @return 验证规则集。
     */
    Validate[] value();
}
