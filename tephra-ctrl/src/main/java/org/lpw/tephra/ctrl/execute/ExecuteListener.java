package org.lpw.tephra.ctrl.execute;

/**
 * 执行配置监听器。
 *
 * @author lpw
 */
public interface ExecuteListener {
    /**
     * 配置通知。
     *
     * @param classExecute  类定义。
     * @param methodExecute 方法定义。
     */
    void definition(Execute classExecute, Execute methodExecute);
}
