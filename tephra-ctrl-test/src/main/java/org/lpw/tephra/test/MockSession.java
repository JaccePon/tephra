package org.lpw.tephra.test;

import org.lpw.tephra.ctrl.context.SessionAdapter;

/**
 * @author lpw
 */
public interface MockSession extends SessionAdapter {
    /**
     * 设置Session ID值。
     *
     * @param id Session ID值。
     */
    void setId(String id);
}
