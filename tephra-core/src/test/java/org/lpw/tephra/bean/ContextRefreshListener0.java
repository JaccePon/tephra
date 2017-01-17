package org.lpw.tephra.bean;

import org.springframework.stereotype.Component;

/**
 * @author lpw
 */
@Component("tephra.bean.context-refresh-listener.0")
public class ContextRefreshListener0 implements ContextRefreshedListener {
    @Override
    public int getContextRefreshedSort() {
        return 1;
    }

    @Override
    public void onContextRefreshed() {
        throw new NullPointerException();
    }
}
