package org.lpw.tephra.ctrl;

/**
 * 调度。用于对请求进行分发。
 *
 * @author lpw
 */
public interface Dispatcher {
    /**
     * 执行服务。
     */
    void execute();
}
