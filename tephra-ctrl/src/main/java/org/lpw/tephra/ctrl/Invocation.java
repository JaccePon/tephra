package org.lpw.tephra.ctrl;

/**
 * 拦截器调用。
 *
 * @author lpw
 */
public interface Invocation {
    /**
     * 执行调用。
     *
     * @return 执行结果。
     * @throws Exception 运行期异常。
     */
    Object invoke() throws Exception;
}
