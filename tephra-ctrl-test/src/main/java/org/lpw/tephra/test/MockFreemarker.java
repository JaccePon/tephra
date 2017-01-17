package org.lpw.tephra.test;

import org.lpw.tephra.ctrl.template.Template;

/**
 * @author lpw
 */
public interface MockFreemarker extends Template {
    /**
     * 获取模板名称。
     *
     * @return 模板名称。
     */
    String getName();

    /**
     * 获取数据。
     *
     * @return 数据。
     */
    Object getData();
}
