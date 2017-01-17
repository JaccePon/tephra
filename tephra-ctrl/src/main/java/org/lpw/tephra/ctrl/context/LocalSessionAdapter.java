package org.lpw.tephra.ctrl.context;

/**
 * 基于字符串的Session适配器实现。
 *
 * @author lpw
 */
public class LocalSessionAdapter implements SessionAdapter {
    protected String id;

    public LocalSessionAdapter(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
