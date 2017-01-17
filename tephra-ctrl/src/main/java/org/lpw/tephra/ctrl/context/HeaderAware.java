package org.lpw.tephra.ctrl.context;

/**
 * @author lpw
 */
public interface HeaderAware {
    /**
     * 设置请求头适配器。
     *
     * @param adapter 请求头适配器。
     */
    void set(HeaderAdapter adapter);
}
