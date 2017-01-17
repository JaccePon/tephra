package org.lpw.tephra.bean;

import org.springframework.stereotype.Component;

/**
 * @author lpw
 */
@Component("tephra.bean.context-refresh-listener.1")
public class ContextRefreshListener1 implements ContextRefreshedListener {
    @Override
    public int getContextRefreshedSort() {
        return 1;
    }

    @Override
    public void onContextRefreshed() {
        ContainerTest.runContextRefreshListener(getContextRefreshedSort());
    }
}
